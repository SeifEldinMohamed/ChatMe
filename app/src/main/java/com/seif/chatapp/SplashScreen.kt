package com.seif.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.seif.chatapp.login_Register.Welcome
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        txt_inc.alpha = 0f
        val splashDuration = 2000L
        txt_inc.animate().setDuration(splashDuration).alpha(1.5f)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        Handler().postDelayed({
            VerifyUserLogging()
        },splashDuration)

    }

    private fun VerifyUserLogging() {
        val uid = FirebaseAuth.getInstance().currentUser

        if (uid == null){
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
            finish()
        }
        else{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}