package com.seif.chatapp.Models

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.seif.chatapp.Adaptes.UserAdapter
import com.seif.chatapp.R
import com.seif.chatapp.ViewFullimage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_item_left.view.*
import kotlinx.android.synthetic.main.message_item_left.view.txt_showmessage
import kotlinx.android.synthetic.main.message_item_right.view.*

class ChatAdapter( mcontext: Context,  mChatList: ArrayList<Chat>,  imageUrl: String) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private val mcontext : Context
    private val mChatList: ArrayList<Chat>
    private val imageUrl: String
    init {
        this.mcontext  = mcontext
        this.mChatList = mChatList
        this.imageUrl = imageUrl
    }



    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var profileimage: CircleImageView? = null
        var showtextmessage: TextView? = null
        var leftimageview: ImageView? = null
        var Righttimageview: ImageView? = null
        var txtSeen: TextView? = null

        init {
            profileimage = itemview.findViewById(R.id.profile_image)
            showtextmessage = itemview.findViewById(R.id.txt_showmessage)
            leftimageview = itemview.findViewById(R.id.image_left)
            Righttimageview = itemview.findViewById(R.id.image_right)
            txtSeen = itemview.findViewById(R.id.txt_seen)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (mChatList[position].sender == currentUser!!.uid) {
            return 0
        } else {
            return 1
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == 0) {
            val v = LayoutInflater.from(mcontext)
                .inflate(R.layout.message_item_right, parent, false)
            return ViewHolder(v)
        } else {
            val v = LayoutInflater.from(mcontext)
                .inflate(R.layout.message_item_left, parent, false)
            return ViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat : Chat = mChatList[position]
        val currentUser = FirebaseAuth.getInstance().currentUser

        Picasso.get().load(imageUrl).into(holder.profileimage)
        Log.d("test",chat.message+"\n")
        // image messages
        if (chat.message == "sent you an image." && chat.url != "") {
            // image message - right side
            if (chat.sender == currentUser!!.uid) {
                // holder.showtextmessage!!.text = ""
                // holder.showtextmessage!!.setBackgroundColor(Color.parseColor("#e6e6e6"))
                holder.showtextmessage!!.visibility = View.GONE
                holder.Righttimageview!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.Righttimageview)

                holder.Righttimageview!!.setOnClickListener {
                    val options = arrayOf(
                        "View full image",
                        "Delete Image",
                        "Cancel"
                    )
                    val builder = AlertDialog.Builder(holder.itemView.context,R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                    builder.setTitle("What do you want?")
                    builder.setItems(options, DialogInterface.OnClickListener{
                        dialog, which ->
                        if (which == 0){
                            val intent = Intent(mcontext,ViewFullimage::class.java)
                            intent.putExtra("url", chat.url)
                            mcontext.startActivity(intent)
                        }
                        else if (which == 1){
                            deleteSendMessage(position, holder)
                        }
                    })
                    builder.show()
                }

            }
            // image message - left side
            else if (chat.sender != currentUser.uid) {
                holder.showtextmessage!!.visibility = View.GONE
                holder.leftimageview!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.leftimageview)

                holder.leftimageview!!.setOnClickListener {
                    val options = arrayOf(
                        "View full image",
                        "Cancel"
                    )
                    val builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")
                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if (which == 0){
                            val intent = Intent(mcontext,ViewFullimage::class.java)
                            intent.putExtra("url", chat.url)
                            mcontext.startActivity(intent)
                        }
                    })
                    builder.show()
                }

            }
        }
        // text messages
        else {
            holder.showtextmessage!!.text = chat.message

            if (currentUser!!.uid == chat.sender){
                holder.showtextmessage!!.setOnClickListener {
                    val options = arrayOf(
                        "Delete Message",
                        "Cancel"
                    )
                    val builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")
                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if (which == 0){
                            deleteSendMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }
        }
        //sent and seen message
        if (position == mChatList.size - 1) {
            if (chat.isseen == "true") {
                holder.txtSeen!!.text = "Seen"
            } else {
                holder.txtSeen!!.text = "Sent"

            }
        } else {
            holder.txtSeen!!.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }
    private fun deleteSendMessage(position: Int, holder: ViewHolder){
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList[position].message).removeValue()
            .addOnSuccessListener {
                Toast.makeText(holder.itemView.context,"Deleted",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(holder.itemView.context,"Not deleted bec : ${it.message}"
                    ,Toast.LENGTH_SHORT).show()
            }
    }

}
