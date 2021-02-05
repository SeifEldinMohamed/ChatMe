package com.seif.chatapp.Adaptes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.seif.chatapp.MessageChat
import com.seif.chatapp.Models.Chat
import com.seif.chatapp.Models.user
import com.seif.chatapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_search_item.view.*

class UserAdapter(var mcontext:Context,var mUser:List<user>,var isChatChecked:Boolean)
    :RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    var lastMessage : String = ""

    class ViewHolder(itemview:View):RecyclerView.ViewHolder(itemview){
        var username = itemview.txt_username
        var userImage = itemview.image_profile
        var online = itemview.image_online
        var offline = itemview.image_offline
        var lastMessges = itemview.txt_last_message

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.user_search_item,parent,false)
        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // user who i sent message to him
        val user: user = mUser[position]
        holder.username.text = user.username
        // we used place holder to display a default pic until the image came from database
        Picasso.get().load(user.profile).placeholder(R.drawable.profile_image)
            .into(holder.userImage)

        // for status feature
        if (isChatChecked){
            getlastMessage(user.uid,holder.lastMessges)
        }
        else{
            holder.lastMessges.visibility = View.GONE
        }


        if (isChatChecked){
            if (user.status == "online"){
                holder.online.visibility = View.VISIBLE
                holder.offline.visibility = View.GONE
            }
            else{
                holder.online.visibility = View.GONE
                holder.offline.visibility = View.VISIBLE
            }
        }
        else{
            holder.offline.visibility = View.GONE
            holder.online.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            // send toUser information to messageChat activity
            val intent = Intent(mcontext, MessageChat::class.java)
            intent.putExtra("touser", user)
            mcontext.startActivity(intent)
        }
    }

    private fun getlastMessage(ChatUserId: String, lastMessges: TextView?) {

        lastMessage = "default message"
        val currentUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val chat = it.getValue(Chat::class.java)
                    if (currentUser != null && chat != null){
                        if (chat.sender == ChatUserId && chat.reciever == currentUser.uid
                            || chat.sender == currentUser.uid && chat.reciever == ChatUserId){
                             lastMessage = chat.message
                        }

                    }
                }
                when(lastMessage){
                    "default message" -> lastMessges!!.text = "No message yet"
                    "sent you an image." -> lastMessges!!.text = "image sent"
                    else -> lastMessges!!.text = lastMessage
                }
                lastMessage = "default message"
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    override fun getItemCount(): Int {
        return mUser.size
    }




}