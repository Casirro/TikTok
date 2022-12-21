package com.casirro.tiktokjava.Activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.databinding.ActivityUserNameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserNameBinding
    private lateinit var database: FirebaseDatabase
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()
        database.reference.child("users").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                binding.editTextTextPersonName2.setText(user?.username)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })





        binding.textView68.setOnClickListener {
            val username: String = binding.editTextTextPersonName2.text.toString()
            val obj = java.util.HashMap<String, Any>()
            obj.put("username", username)

            database.reference.child("users").child(FirebaseAuth.getInstance().uid!!)
                .updateChildren(obj)
            finish()

        }
        binding.textView67.setOnClickListener {
            finish()
        }


    }
}