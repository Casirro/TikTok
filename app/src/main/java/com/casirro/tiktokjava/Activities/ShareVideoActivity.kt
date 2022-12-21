package com.casirro.tiktokjava.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.Modules.UserVideo
import com.casirro.tiktokjava.Modules.Video
import com.casirro.tiktokjava.databinding.ActivityShareVideoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class ShareVideoActivity : AppCompatActivity() {
    var binding: ActivityShareVideoBinding? = null
    var database: FirebaseDatabase? = null
    var userVideo: User? = null
    var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareVideoBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar!!.hide()
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Nahrávání videa...")
        dialog!!.setCancelable(false)
        database = FirebaseDatabase.getInstance()
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val uri: String? = intent.getStringExtra("VideoUri")
        val video: Uri = Uri.parse(uri)
        database!!.reference.child("users").child((FirebaseAuth.getInstance().uid)!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userVideo = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        binding!!.imageView35.setOnClickListener { finish() }
        binding!!.view28.setOnClickListener {
            dialog!!.show()
            val date = Date()
            val reference: StorageReference =
                storage.reference.child("Videos").child(date.time.toString() + "")
            reference.putFile(video)
                .addOnCompleteListener {
                    reference.downloadUrl
                        .addOnSuccessListener { uri ->
                            val name = userVideo!!.name
                            val profileImage = userVideo!!.profileImage
                            val videoPath: String = uri.toString()
                            val videoId: String? =
                                database!!.reference.push().key
                            val caption: String =
                                binding!!.editTextTextPersonName.text.toString()
                            val obj = HashMap<String, Any>()
                            val userId = FirebaseAuth.getInstance().uid
                            obj["name"] = name!!
                            obj["profileImage"] = profileImage!!


                            val video =
                                Video(videoPath, caption, videoId , userId )
                            database!!.reference.child("videos")
                                .child((FirebaseAuth.getInstance().uid)!!)
                                .child("userVideos").child(
                                    (videoId)!!
                                ).setValue(video)
                            database!!.reference.child("videos").child(FirebaseAuth.getInstance().uid!!).updateChildren(obj)
                            startActivity(
                                Intent(
                                    this,
                                    ProfileActivity::class.java
                                )
                            )
                            dialog!!.dismiss()
                            finish()

                        }
                }
        }
        binding!!.view19.setOnClickListener { binding!!.editTextTextPersonName.setText("#") }
        binding!!.view20.setOnClickListener { binding!!.editTextTextPersonName.setText("@") }
    }
}