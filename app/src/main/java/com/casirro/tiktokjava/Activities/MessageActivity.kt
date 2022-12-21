package com.casirro.tiktokjava.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.casirro.tiktokjava.Adapters.ChatUserAdapter
import com.casirro.tiktokjava.Adapters.NewChatUserAdapter
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var users: ArrayList<User>
    var userChatAdapter: ChatUserAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        database = FirebaseDatabase.getInstance()
        users = ArrayList()

        userChatAdapter = ChatUserAdapter(this, users)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        binding.recyclerView.adapter = userChatAdapter

        binding.discoverImage.setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        binding.newImage.setOnClickListener{
            startActivity(
                Intent(
                    this,
                    CameraActivity::class.java
                )
            )
        }

        binding.imageView9.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        binding.meImage.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        binding.imageView57.setOnClickListener{
            val intent = Intent(this, AddChat::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        database.reference.child("usersFriends").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (snapshot1 in snapshot.children) {
                    val user = snapshot1.getValue(User::class.java)
                    users.add(user!!)

                }
                userChatAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }
}