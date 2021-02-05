package com.seif.chatapp.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.seif.chatapp.Adaptes.UserAdapter
import com.seif.chatapp.Models.user
import com.seif.chatapp.R
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private var userAdapter : UserAdapter? = null
    private var mUser : List<user>? = null
    private var recyclerView:RecyclerView? = null
    private var searchedittext:EditText? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.rec_search)
       // recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchedittext = view.findViewById(R.id.edit_search_user)
        recyclerView!!.addItemDecoration(DividerItemDecoration(context,OrientationHelper.VERTICAL))


        mUser = ArrayList()
        retriveAllUsers()
        searchedittext!!.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchForUser(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        return view
    }

    private fun retriveAllUsers() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().reference.child("users")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (edit_search_user==null) return
                if (edit_search_user.text.toString() == ""){
                    (mUser as ArrayList<user>).clear()
                    snapshot.children.forEach {
                        val user = it.getValue(user::class.java)
                        if (user!=null && user.uid != userId){
                            (mUser as ArrayList<user>).add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!,mUser!!,false)
                    recyclerView!!.adapter = userAdapter
                    recyclerView!!.addItemDecoration(DividerItemDecoration(context!!,OrientationHelper.VERTICAL))
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun searchForUser(str:String){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("users").orderByChild("search")
            .startAt(str).endAt(str + "\uf8ff")
        queryUsers.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser as ArrayList<user>).clear()
                snapshot.children.forEach {
                    var user = it.getValue(user::class.java)
                    if (user != null && user.uid != userId) {
                        (mUser as ArrayList<user>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!,mUser!!,false)
                recyclerView!!.adapter = userAdapter
                recyclerView!!.addItemDecoration(DividerItemDecoration(context!!,OrientationHelper.VERTICAL))

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

}