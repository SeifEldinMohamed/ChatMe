package com.seif.chatapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.seif.chatapp.Models.user
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.android.synthetic.main.activity_visit_user_profile.*

class VisitUserProfile : AppCompatActivity() {
    private var visituser: user? = null
    var ref: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)

        visituser = intent.getParcelableExtra("visituser")

        Picasso.get().load(visituser!!.profile).into(set_profile_image_visit)
        Picasso.get().load(visituser!!.cover).into(set_cover_image_visit)
        txt_username_settings_visit.text = visituser!!.username

        val currentUser = FirebaseAuth.getInstance().currentUser

        ref = FirebaseDatabase.getInstance().reference.child("users").child(currentUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: user? = snapshot.getValue(user::class.java)
                    // show MainUser informaion in toolbar of visitProfile Activity
                    Picasso.get().load(user!!.profile).into(user_profileImage_visit)
                    user_name_visit.text = user.username
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        btn_SendMessage.setOnClickListener {
            val intent = Intent(this, MessageChat::class.java)
            intent.putExtra("touser", visituser)
            startActivity(intent)
        }
        set_facebook_visit.setOnClickListener {
            val uri = Uri.parse(visituser!!.face)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        set_insta_visit.setOnClickListener {
            val uri = Uri.parse(visituser!!.insta)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        set_website_visit.setOnClickListener {
            val uri = Uri.parse(visituser!!.website)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        set_profile_image_visit.setOnClickListener {
            val intent = Intent(this, ViewFullimage::class.java)
            intent.putExtra("username", visituser!!.username)
            intent.putExtra("url", visituser!!.profile)
            startActivity(intent)
        }
        set_cover_image_visit.setOnClickListener {
            val intent = Intent(this, ViewFullimage::class.java)
            intent.putExtra("username", visituser!!.username)
            intent.putExtra("url", visituser!!.cover)
            startActivity(intent)
        }

    }
}