package com.seif.chatapp.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class user(var uid:String,var username:String,var profile:String,var cover:String,var status:String
                ,var search:String,var face:String,var insta:String,var password:String,var email:String
                ,var website:String):Parcelable{

    constructor():this("","","","","",""
        ,"","","","","")

}