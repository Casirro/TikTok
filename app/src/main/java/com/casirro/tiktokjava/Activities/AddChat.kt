package com.casirro.tiktokjava.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.casirro.tiktokjava.Adapters.NewChatUserAdapter
import com.casirro.tiktokjava.Adapters.SoftInputAssist
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.databinding.ActivityAddChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddChat : AppCompatActivity() {
    private lateinit var binding: ActivityAddChatBinding
    private lateinit var database: FirebaseDatabase
    lateinit var users: ArrayList<User>
    private lateinit var softInputAssist: SoftInputAssist
    var friendsAdapter: NewChatUserAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        softInputAssist = SoftInputAssist(this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        database = FirebaseDatabase.getInstance()

        binding.imageView55.setOnClickListener{ finish() }
        users = ArrayList()
        friendsAdapter = NewChatUserAdapter(this, users)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        binding.recyclerView.adapter = friendsAdapter

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchUsers(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
        database.reference.child("following").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (snapshot1 in snapshot.children) {
                    val user: User? = snapshot1.getValue(User::class.java)
                    if (user != null) users.add(user)
                }

                friendsAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun searchUsers(s: String) {
        val query = FirebaseDatabase.getInstance().getReference("following").child(FirebaseAuth.getInstance().uid!!).orderByChild("username")
            .startAt(s)
            .endAt(s + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    users.add(user!!)
                }
                friendsAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }







    }
