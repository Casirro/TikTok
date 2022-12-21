package com.casirro.tiktokjava.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var database: FirebaseDatabase
    private var user: User? = null
    private var selectedImage: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()

        database.reference.child("users").child(FirebaseAuth.getInstance().uid.toString()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        database.reference.child("users").child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(
                        User::class.java
                    )
                    binding.textView64.text = user?.name
                    binding.textView65.text = user?.username
                    Glide.with(applicationContext).load(user?.profileImage)
                        .placeholder(R.drawable.avatar).into(
                            binding.imageView1
                        )
                }

                override fun onCancelled(error: DatabaseError) {}
            })


        binding.view28.setOnClickListener {
            intent = Intent(this, UserNameActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        binding.view29.setOnClickListener {
            intent = Intent(this, BioActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        binding.imageView48.setOnClickListener {
            finish()
        }

        binding.imageView1.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 69)
        }

        binding.view27.setOnClickListener{
            val intent = Intent(this, NameActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)

        }








    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!!.data != null) {
            val uri = data.data // filepath
            binding.imageView1.setImageURI(uri)
            val storage = FirebaseStorage.getInstance()
            val time = Date().time
            val reference =
                storage.reference.child("Profiles").child(FirebaseAuth.getInstance().uid!!)
                    .child(time.toString() + "")
            reference.putFile(uri!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener { uri ->
                        database.reference.child("users")
                            .child(FirebaseAuth.getInstance().uid!!).child("profileImage")
                            .setValue(uri.toString())

                    }
                }
            }
        }
        selectedImage = data.data
    }
}