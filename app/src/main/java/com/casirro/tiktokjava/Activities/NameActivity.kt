package com.casirro.tiktokjava.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.databinding.ActivityNameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NameActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private var user: User? = null
    private lateinit var binding: ActivityNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()
        database.reference.child("users").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                binding.editTextTextPersonName2.setText(user?.name)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })





        binding.textView68.setOnClickListener {
            val name: String = binding.editTextTextPersonName2.text.toString()
            val obj = java.util.HashMap<String, Any>()
            obj["name"] = name

            database.reference.child("users").child(FirebaseAuth.getInstance().uid!!)
                .updateChildren(obj)
            finish()

        }
        binding.textView67.setOnClickListener {
            finish()
        }

    }
}