package com.seif.chatapp.Notifications

class Data {
    private var user : String = ""
    private var icon = 0
    private var body : String = ""
    private var title : String = ""
    private var sented : String = ""

    constructor(){}
    constructor(user: String, icon: Int, body: String, title: String, sented: String) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sented = sented
    }
    fun getuser():String?{
        return user
    }
    fun setuser(user: String){
        this.user = user
    }
    fun geticon():Int{
        return icon
    }
    fun seticon(icon: Int){
        this.icon = icon
    }
    fun getbody():String?{
        return body
    }
    fun setbody(body: String){
        this.body = body
    }
    fun gettitle():String?{
        return title
    }
    fun settitle(title: String){
        this.title = title
    }
    fun getsented():String?{
        return sented
    }
    fun setsented(seted: String){
        this.sented = sented
    }

}