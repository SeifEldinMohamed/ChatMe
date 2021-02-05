package com.seif.chatapp.login_Register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.seif.chatapp.MainActivity
import com.seif.chatapp.Models.user
import com.seif.chatapp.R
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    var firebaseid : String = ""
    var email : String = ""
    var password : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(toolbar_Register)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Register")

        r_btn.setOnClickListener {
            authntication()
        }
    }
    private fun authntication() {
        val username = username_Register.text.toString()
         email = email_Register.text.toString()
         password = password_Register.text.toString()

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Enter your email and password!", Toast.LENGTH_SHORT).show()
            return
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Enter your Email!", Toast.LENGTH_SHORT).show()
            return
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter your Password!", Toast.LENGTH_SHORT).show()
            return
        } else if (username.isEmpty()) {
            Toast.makeText(this, "Enter your Name!", Toast.LENGTH_SHORT).show()
            return
        }
        // create user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                     firebaseid = FirebaseAuth.getInstance().currentUser!!.uid
                    Toast.makeText(this, "successfully create user with id = $firebaseid", Toast.LENGTH_SHORT).show()
                      uploadDataToDatabase()
                }

            }.addOnFailureListener {
                Toast.makeText(
                    this, "Failed to create user $username bec: ${it.message}", Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadDataToDatabase() {

        val ref = FirebaseDatabase.getInstance().reference.child("users").child(firebaseid)
        val userr = user(firebaseid,username_Register.text.toString()
            ,"https://firebasestorage.googleapis.com/v0/b/chatapp-3a816.appspot.com/o/profile_image.png?alt=media&token=d2fdeb79-8a78-45e4-b444-b201c54b6354"
            ,"https://firebasestorage.googleapis.com/v0/b/chatapp-3a816.appspot.com/o/coveer_image.jpg?alt=media&token=aa02278a-61ff-4829-87b8-ee3827f985da"
            ,"offline",username_Register.text.toString().toLowerCase()
            ,"https://m.facebook.com","https://m.instagram.com",password,email
            ,"https://www.google.com")

        ref.setValue(userr).addOnCompleteListener {
            if (it.isSuccessful){
                // else if it's successfull
                Toast.makeText(this, "successfully update user data to database", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }.addOnFailureListener {
            Toast.makeText(
                this, "Failed to upload data bec: $it", Toast.LENGTH_SHORT
            ).show()
        }

    }

}