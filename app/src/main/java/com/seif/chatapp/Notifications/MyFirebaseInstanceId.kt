package com.seif.chatapp.Notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService


// let's say that user a send a message to user b then for each notificaion we need a unique token
// thay will recognize tha spedific notification fron device to device
class MyFirebaseInstanceId : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val currentUser = FirebaseAuth.getInstance().currentUser
        val refreachToken = FirebaseInstanceId.getInstance().token

        if (currentUser!= null){
            updateToken(refreachToken)
        }
    }

    private fun updateToken(refreachToken: String?) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().getReference().child("Tokens")
        val token = Token(refreachToken!!)
        ref.child(currentUser!!.uid).setValue(token)

    }
}