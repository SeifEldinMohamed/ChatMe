package com.seif.chatapp.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Chat(var sender : String,var reciever : String,var message : String,var isseen : String
           ,var url : String,var messageId : String):Parcelable {
    constructor():this("","","","","","")
}