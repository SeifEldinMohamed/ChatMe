package com.seif.chatapp.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.seif.chatapp.Adaptes.UserAdapter
import com.seif.chatapp.Models.ChatList
import com.seif.chatapp.Models.user
import com.seif.chatapp.Notifications.Token
import com.seif.chatapp.R
import kotlinx.android.synthetic.main.fragment_chats.*

class ChatsFragment : Fragment() {
    private var mUser: List<user>? = null
    private var UserChatlist: List<ChatList>? = null
    lateinit var recyclerViewChatlist: RecyclerView
    var currentUser: FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        recyclerViewChatlist = view.findViewById(R.id.recyclerView_chatlist)
        recyclerViewChatlist.setHasFixedSize(true)
        recyclerViewChatlist.layoutManager = LinearLayoutManager(context)
        currentUser = FirebaseAuth.getInstance().currentUser

        UserChatlist = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("chatList")
            .child(currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (UserChatlist as ArrayList).clear()
                    snapshot.children.forEach {
                        val chatList = it.getValue(ChatList::class.java)
                        (UserChatlist as ArrayList).add(chatList!!)
                    }
                    getChatlist()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        updateToken(FirebaseInstanceId.getInstance().token)
        return view
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(currentUser!!.uid).setValue(token1)
    }

    fun getChatlist() {
        mUser = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser as ArrayList).clear()
                if (snapshot.exists()){
                    snapshot.children.forEach {
                        val user = it.getValue(user::class.java)
                        for (eachChatlist in UserChatlist!!) {
                            if (user!!.uid == eachChatlist.id) {
                                (mUser as ArrayList<user>).add(user)
                            }
                        }
                    }
                    val userAdapter = UserAdapter(context, (mUser as ArrayList<user>), true)
                    recyclerViewChatlist.adapter = userAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun updateStatus(status : String){
        val currentser = FirebaseAuth.getInstance().currentUser

        val ref = FirebaseDatabase.getInstance().reference.child("users").child(currentser!!.uid)
        val hashmap = HashMap<String, Any>()
        hashmap["status"] = status
        ref.updateChildren(hashmap)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        updateStatus("online")

    }


}