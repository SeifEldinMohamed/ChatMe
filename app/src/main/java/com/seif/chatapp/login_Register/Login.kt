package com.seif.chatapp.login_Register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.seif.chatapp.MainActivity
import com.seif.chatapp.R
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // login toolbar
        setSupportActionBar(toolbar_login)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        btn_login.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = email_login.text.toString()
        val password = password_login.text.toString()
        // check if all the data are not empty
        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Enter your email and password!", Toast.LENGTH_SHORT).show()
            return
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Enter your Email!", Toast.LENGTH_SHORT).show()
            return
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter your Password!", Toast.LENGTH_SHORT).show()
            return
        }
        val ref = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        ref.addOnCompleteListener {
            if (!it.isSuccessful) return@addOnCompleteListener
            // else if it's successful
            Toast.makeText(this, "Logging...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to logging : $it", Toast.LENGTH_SHORT).show()
        }
    }
}

/// control + shift + j -> to make to lines to be in same line ( remove extra spaces bet two lines )