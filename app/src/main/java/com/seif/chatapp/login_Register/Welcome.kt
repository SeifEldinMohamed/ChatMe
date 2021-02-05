package com.seif.chatapp.login_Register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seif.chatapp.R
import kotlinx.android.synthetic.main.activity_welcome.*

class Welcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        btn_welcome_Register.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        btn_welcome_login.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}