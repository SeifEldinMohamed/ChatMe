package com.seif.chatapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.seif.chatapp.Fragments.ApiService
import com.seif.chatapp.Models.Chat
import com.seif.chatapp.Models.ChatAdapter
import com.seif.chatapp.Models.user
import com.seif.chatapp.Notifications.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_message_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageChat : AppCompatActivity() {
    var currentUser : FirebaseUser? = null
    private val RequestCode = 400
    var toUser :user? = null
    var chatsAdapter : ChatAdapter? = null
    var mchatList : ArrayList<Chat>? = null
    var ref : DatabaseReference? = null
    var notify = false
    var apiService : ApiService? = null
    lateinit var recycler_view_chats:RecyclerView
    var userid : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        setSupportActionBar(toolbar_chatMessage)

        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar_chatMessage.setNavigationOnClickListener {
            finish()
        }
        currentUser = FirebaseAuth.getInstance().currentUser
        toUser = intent.getParcelableExtra("touser")

        // visit talking user profile
        username_chatMessage.setOnClickListener {
            val intet = Intent(this,VisitUserProfile::class.java)
            intet.putExtra("visituser",toUser)
            startActivity(intet)
        }
        // visit talking user profile Image
        user_profileImage_chatMessage.setOnClickListener{
            val intet = Intent(this,ViewFullimage::class.java)
            intet.putExtra("username", toUser!!.username)
            intet.putExtra("url", toUser!!.profile)
            startActivity(intet)
        }

        // put api of the fcm notification
        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(ApiService::class.java)

        recycler_view_chats = findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recycler_view_chats.layoutManager = linearLayoutManager

        // if the toUser is null so we entered from notification and have to use userid from MyFirebaseMessaging
        if (toUser == null){
             userid = intent.getStringExtra("userid")
            ref = FirebaseDatabase.getInstance().reference.child("users").child(userid!!)
            ref!!.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val user : user? = snapshot.getValue(user::class.java)
                        // show user informaion in toolbar of chat Activity
                        username_chatMessage.text = user!!.username
                        Picasso.get().load(user.profile).into(user_profileImage_chatMessage)
                        retrieveData(currentUser!!.uid, userid!!, user!!.profile)

                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
        else{ // use the toUser as we entered from mainActivity normally
            ref = FirebaseDatabase.getInstance().reference.child("users").child(toUser!!.uid)
            ref!!.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val user : user? = snapshot.getValue(user::class.java)
                        // show user informaion in toolbar of chat Activity
                        username_chatMessage.text = user!!.username
                        Picasso.get().load(user.profile).into(user_profileImage_chatMessage)
                        retrieveData(currentUser!!.uid, toUser!!.uid, user!!.profile)
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        btn_sendMessage.setOnClickListener {
            notify = true
            val message = edit_chatMessage.text.toString().trim()
            if (message == ""){
                Toast.makeText(this,"write something first!",Toast.LENGTH_SHORT).show()
            }
            else{
                if (toUser==null){
                    sendMessageToUser(currentUser!!.uid, userid!!, message)

                }else{
                    sendMessageToUser(currentUser!!.uid, toUser!!.uid, message)
                }
            }
            edit_chatMessage.setText("")
        }

        img_Attachimage.setOnClickListener {
            notify = true
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"pick image"),RequestCode)
        }

        if (toUser == null) {
            seenMessage((userid!!))
        }
        else{
            seenMessage(toUser!!.uid)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data != null) {
            val progressbar = ProgressDialog(this,R.style.Theme_AppCompat_DayNight_Dialog_Alert)
            progressbar.setMessage("please wait, image is sending...")
            val imageUrl = data.data
            val storageRefrence = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filepath =  storageRefrence.child("$messageId.jpg")
            progressbar.show()
            filepath.putFile(imageUrl!!).addOnSuccessListener {
                Toast.makeText(this,
                    "Successfully send image to storage $it",
                    Toast.LENGTH_SHORT).show()
                filepath.downloadUrl.addOnSuccessListener {

                    val mapSendMessage = HashMap<String, Any>()
                    mapSendMessage["sender"] = currentUser!!.uid
                    if (toUser == null){
                        mapSendMessage["reciever"] = userid!!
                    }
                    else{
                        mapSendMessage["reciever"] = toUser!!.uid

                    }
                    mapSendMessage["message"] = "sent you an image."
                    mapSendMessage["isseen"] = "false"
                    mapSendMessage["url"] = it.toString()
                    mapSendMessage["messageId"] = messageId.toString()

                    ref.child("Chats").child(messageId!!).setValue(mapSendMessage)
                        .addOnSuccessListener {
                            progressbar.dismiss()
                            val refrence = FirebaseDatabase.getInstance().reference
                                .child("users").child(currentUser!!.uid)
                            refrence.addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val user = snapshot.getValue(user::class.java)
                                    if(notify){
                                        if (toUser == null){
                                            sendNotification(userid!!, user!!.username, "sent you an image.")
                                        }
                                        else{
                                            sendNotification(toUser!!.uid, user!!.username, "sent you an image.")

                                        }
                                    }
                                    notify = false
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })

                        }

                }.addOnFailureListener {
                    Toast.makeText(this, "Error : ${it.message}", Toast.LENGTH_LONG).show()
                    progressbar.dismiss()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error : ${it.message}", Toast.LENGTH_LONG).show()
                progressbar.dismiss()
            }
        }
    }
        fun sendMessageToUser(senderId: String, recieverId: String, message: String) {
            val refrence = FirebaseDatabase.getInstance().reference
            val messagekey = refrence.push().key.toString()
            val messagemap = HashMap<String, Any>()
            messagemap["sender"] = senderId
            messagemap["reciever"] = recieverId
            messagemap["message"] = message
            messagemap["isseen"] = "false"
            messagemap["url"] = ""
            messagemap["messageId"] = messagekey
            Log.d("not","f")
            var chatListRefrence = FirebaseDatabase.getInstance().reference
            refrence.child("Chats").child(messagekey).setValue(messagemap)
                .addOnSuccessListener {
                    if (toUser == null) {
                         chatListRefrence = FirebaseDatabase.getInstance().reference
                            .child("chatList").child(currentUser!!.uid)
                            .child(userid!!)
                    }
                    else{
                        chatListRefrence = FirebaseDatabase.getInstance().reference
                            .child("chatList").child(currentUser!!.uid)
                            .child(toUser!!.uid)
                    }

                        chatListRefrence.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (!snapshot.exists()){
                                    if (toUser == null) {
                                        chatListRefrence.child("id").setValue(userid)
                                    }
                                    else{
                                        chatListRefrence.child("id").setValue(toUser!!.uid)

                                    }
                                }
                                var chatListRecieverRef = FirebaseDatabase.getInstance().reference
                                // for the reciever user
                                if (toUser == null) {
                                    chatListRecieverRef = FirebaseDatabase.getInstance().reference
                                        .child("chatList").child(userid!!)
                                        .child(currentUser!!.uid)
                                    chatListRecieverRef.child("id").setValue(currentUser!!.uid)

                                }
                                else{
                                    chatListRecieverRef = FirebaseDatabase.getInstance().reference
                                        .child("chatList").child(toUser!!.uid)
                                        .child(currentUser!!.uid)
                                    chatListRecieverRef.child("id").setValue(currentUser!!.uid)

                                }

                            }
                            override fun onCancelled(error: DatabaseError) {

                            }
                        })

                }

            // implement the push notification using fcm
            val userrefrence = FirebaseDatabase.getInstance().reference
                .child("users").child(currentUser!!.uid)
            userrefrence.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(user::class.java)
                    if(notify){
                        sendNotification(recieverId, user!!.username, message)
                    }
                    notify = false
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    private fun sendNotification(recieverId: String,username: String, message: String){
        val ref = FirebaseDatabase.getInstance().getReference().child("Tokens")
        val query = ref.orderByKey().equalTo(recieverId)
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val token : Token? = it.getValue(Token::class.java)
                    if (toUser == null) {
                        val data = Data(
                            currentUser!!.uid,
                            R.mipmap.ic_launcher,
                            "$username: $message",
                            "New Message",
                            userid!!
                        )
                        val sender = Sender(data!!, token!!.getToken().toString())
                        apiService!!.sendNotification(sender).enqueue(object : Callback<MyResponse>{
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200){
                                    if (response.body()!!.success != 1){
                                        Toast.makeText(this@MessageChat,"Failed, Nothing happend",Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }
                        })
                    }
                    else{
                        val data = Data(
                            currentUser!!.uid,
                            R.mipmap.ic_launcher,
                            "$username: $message",
                            "New Message",
                            toUser!!.uid
                        )
                        val sender = Sender(data!!, token!!.getToken().toString())
                        apiService!!.sendNotification(sender).enqueue(object : Callback<MyResponse>{
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200){
                                    if (response.body()!!.success != 1){
                                        Toast.makeText(this@MessageChat,"Failed, Nothing happend",Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }
                        })
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
    private fun retrieveData(senderId: String, recieverId: String, RecieverImgUrl: String) {
        mchatList = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mchatList!!.clear()
                snapshot.children.forEach {
                    val chat = it.getValue(Chat::class.java)
                    if (chat!!.reciever == senderId && chat!!.sender == recieverId
                        || chat!!.reciever == recieverId && chat!!.sender == senderId){
                        mchatList!!.add(chat)
                    }
                    chatsAdapter = ChatAdapter(this@MessageChat, mchatList!!, RecieverImgUrl)
                    recycler_view_chats.adapter = chatsAdapter

                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    var seenListener : ValueEventListener? = null
    private fun seenMessage(userId : String){
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val chat = it.getValue(Chat::class.java)
                    if (chat!!.reciever == currentUser!!.uid && chat!!.sender == userId ){
                        val hashmap = HashMap<String,Any>()
                        hashmap["isseen"] = "true"
                        it.ref.updateChildren(hashmap)
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onPause() {
        super.onPause()
        // to update user status as offline
        ref!!.removeEventListener(seenListener!!)
    }
    }
