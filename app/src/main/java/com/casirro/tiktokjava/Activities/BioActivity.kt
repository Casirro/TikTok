package com.casirro.tiktokjava.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.databinding.ActivityBioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlin.collections.HashMap

class BioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBioBinding
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        database = FirebaseDatabase.getInstance()

        database.reference.child("users").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.editTextTextPersonName2.setText(user?.bio)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.textView68.setOnClickListener {
            val bio: String = binding.editTextTextPersonName2.text.toString()
            val obj = HashMap<String, Any>()
            obj["bio"] = bio

            database.reference.child("users").child(FirebaseAuth.getInstance().uid.toString())
                .updateChildren(obj)
            finish()
        }
        binding.textView67.setOnClickListener{
            finish()
        }


    }
}