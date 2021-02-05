package com.seif.chatapp.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.solver.state.Reference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.seif.chatapp.Models.user
import com.seif.chatapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.edittext_alertdialog.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.util.*
import kotlin.collections.HashMap

class SettingsFragment : Fragment() {
    private val RequestCode = 200
    var storageRef: StorageReference? = null
    var currentUser: FirebaseUser? = null
    var coverChecker: String? = ""
    var socialChecker: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")
        currentUser = FirebaseAuth.getInstance().currentUser


        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val myUser = snapshot.getValue(user::class.java)
                    if (txt_username_settings == null) return
                    txt_username_settings.text = myUser!!.username
                    Picasso.get().load(myUser!!.profile).into(set_profile_image)
                    Picasso.get().load(myUser.cover).into(set_cover_image)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        view.set_profile_image.setOnClickListener {
            openPhotoSelector()
            coverChecker = "profile"
        }
        view.set_cover_image.setOnClickListener {
            openPhotoSelector()
            coverChecker = "cover"
        }
        view.set_facebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }
        view.set_insta.setOnClickListener {
            socialChecker = "insta"
            setSocialLinks()
        }
        view.set_website.setOnClickListener {
            socialChecker  = "website"
            setSocialLinks()
        }

        return view
    }

    private fun setSocialLinks() {
        val builder = AlertDialog.Builder(context,R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        val inflater = layoutInflater
        val dialoglayout = inflater.inflate(R.layout.edittext_alertdialog,null)
        val editText = dialoglayout.findViewById<EditText>(R.id.editText)
        if (socialChecker == "website"){
            builder.setTitle("write Url:")
                editText.hint = "e.g https://www.google.com"
        }
        else if(socialChecker == "facebook" ){
            builder.setTitle("write Url:")
            editText.hint = "e.g https://www.facebook.com"

        }
        else{
         builder.setTitle("write username:")
            editText.hint = "e.g seifmohamed_11"
        }
        builder.setView(dialoglayout)
        builder.setPositiveButton("Create",DialogInterface.OnClickListener { dialog, which ->
            val str = editText.text.toString()
            if (str.isEmpty()){
                Toast.makeText(context,"please write something....",Toast.LENGTH_SHORT).show()
            }
            else{
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun saveSocialLink(str:String) {
            val mapSocial = HashMap<String,Any>()
        when(socialChecker){
            "facebook" -> mapSocial["face"] = str
            "insta" -> mapSocial["insta"] = "https://www.instagram.com/$str/"
            "website" -> mapSocial["website"] = str
        }
        val refrence = FirebaseDatabase.getInstance().reference.child("users")
            .child(currentUser!!.uid)
            refrence.updateChildren(mapSocial).addOnSuccessListener {
                Toast.makeText(context,"Updated Successfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                    Toast.makeText(context,"Error : ${it.message}",Toast.LENGTH_SHORT).show()

                }
    }

    private fun openPhotoSelector() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, RequestCode)
    }

    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            //Picasso.get().load(selectedPhotoUri).into(set_profile_image)
            Toast.makeText(context, "uploading image....", Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressbar = ProgressDialog(context)
        progressbar.setMessage("image is uploaded Successfully, please wait...")
        progressbar.show()
        if (selectedPhotoUri != null) {
            val filename = currentUser!!.uid
            val randomuid = UUID.randomUUID().toString()
            val ref =
                FirebaseStorage.getInstance().getReference("/User Images/$filename/$randomuid")

            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "Successfully uploading image to storage ${it.metadata!!.path}",
                        Toast.LENGTH_LONG
                    ).show()

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("re", "file location : $it")
                        if (coverChecker == "cover") {
                            var mapcoverimage = HashMap<String, Any>()
                            mapcoverimage["cover"] = it.toString()
                            var refrence = FirebaseDatabase.getInstance().reference.child("users")
                                .child(currentUser!!.uid)
                            refrence.updateChildren(mapcoverimage)
                            coverChecker = ""

                        } else {
                            var mapprofileimage = HashMap<String, Any>()
                            mapprofileimage["profile"] = it.toString()
                            var refrence = FirebaseDatabase.getInstance().reference.child("users")
                                .child(currentUser!!.uid)
                            refrence.updateChildren(mapprofileimage)
                            coverChecker = ""

                        }
                        progressbar.dismiss()

                    }.addOnFailureListener {
                        Log.d("re", "Error in file location of photo: $it")
                    }

                }.addOnFailureListener {
                    Toast.makeText(context, "Error in uploading image: $it", Toast.LENGTH_LONG)
                        .show()
                }


        }


    }


}