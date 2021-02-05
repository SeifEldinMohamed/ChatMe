package com.seif.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.seif.chatapp.Adaptes.viewPagerAdapter
import com.seif.chatapp.Fragments.ChatsFragment
import com.seif.chatapp.Fragments.SearchFragment
import com.seif.chatapp.Fragments.SettingsFragment
import com.seif.chatapp.Models.Chat
import com.seif.chatapp.Models.user
import com.seif.chatapp.login_Register.Welcome
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var currentuser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentuser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("users").child(currentuser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: user? = snapshot.getValue(user::class.java)
                    //  touser = intent.getParcelableExtra<user>("id")
                    user_name.text = user!!.username
                    // we used place holder to display a default pic until the image came from database
                    Picasso.get().load(user.profile).placeholder(R.drawable.profile_image)
                        .into(user_profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        setSupportActionBar(toolbar_settings)
        // to vanishes app name from toolbar
        supportActionBar!!.title = ""

        // show the unreaded messages in the chat tab by making counter for unreaded messages
        val refrence = FirebaseDatabase.getInstance().reference.child("Chats")
        refrence.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // make object from viewPagerAdapter class and give it supportFragmentManager
                val viewPagerAdapter = viewPagerAdapter(supportFragmentManager)
                var countUnreadMessages = 0

                snapshot.children.forEach {
                    val chat = it.getValue(Chat::class.java)
                    if (chat!!.reciever == currentuser!!.uid && chat.isseen == "false") {
                        countUnreadMessages += 1
                    }
                }
                if (countUnreadMessages == 0) {
                    viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
                } else {
                    viewPagerAdapter.addFragment(ChatsFragment(), "Chats ($countUnreadMessages)")
                }
                viewPagerAdapter.addFragment(SearchFragment(), "Search")
                viewPagerAdapter.addFragment(SettingsFragment(), "Settings")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signOut -> {
                val intent = Intent(this, Welcome::class.java)
                // to prevent it from go back to wellcome activity unless logout button is clicked
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }
        }
        return false
    }

    private fun updateStatus(status: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("users").child(currentuser!!.uid)
        val hashmap = HashMap<String, Any>()
        hashmap["status"] = status
        ref.updateChildren(hashmap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")

    }


}