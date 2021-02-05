package com.seif.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_fullimage.*

class ViewFullimage : AppCompatActivity() {
    private var imageUrl:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_fullimage)

        setSupportActionBar(toolbar_chatMessage_fullView)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
        toolbar_chatMessage_fullView.setNavigationOnClickListener {
            finish()
        }
        val username = intent.getStringExtra("username")
        username_chatMessage_fullView.text = username
        imageUrl = intent.getStringExtra("url")!!
        Picasso.get().load(imageUrl).into(img_fullView)

    }
}