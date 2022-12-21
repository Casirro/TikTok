package com.casirro.tiktokjava.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.casirro.tiktokjava.Adapters.VideosAdapter
import com.casirro.tiktokjava.Adapters.VideosAdapter1
import com.casirro.tiktokjava.Modules.ExoPlayerItem
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.Modules.UserVideo
import com.casirro.tiktokjava.Modules.Video
import com.casirro.tiktokjava.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var database: FirebaseDatabase? = null
    private val videos: MutableList<Video> = ArrayList()
    var users: MutableList<User> = ArrayList()
    var videosAdapter: VideosAdapter? = null
    var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        users = ArrayList()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.recyclerView.layoutManager = layoutManager
        val mSnapHelper: SnapHelper = PagerSnapHelper()
        mSnapHelper.attachToRecyclerView(binding.recyclerView)
        videosAdapter = VideosAdapter(videos, this)
        binding.recyclerView.adapter = videosAdapter


        database = FirebaseDatabase.getInstance()
        FirebaseMessaging.getInstance()
            .token
            .addOnSuccessListener { token ->
                if (FirebaseAuth.getInstance().uid != null) {
                    val map = HashMap<String, Any>()
                    map["token"] = token
                    database!!.reference
                        .child("users")
                        .child(FirebaseAuth.getInstance().uid!!)
                        .updateChildren(map)
                    //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                }
            }

        database!!.reference.child("videos").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                videos.clear()
                for(snapshot1 in snapshot.children){
                    for (dataSnapshot in snapshot1.child("userVideos").children){
                        val video = dataSnapshot.getValue(Video::class.java)
                        videos.add(video!!)
                    }
                }
                videos.shuffle()
                videosAdapter!!.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {}
        })
        binding.discoverImage.setOnClickListener{
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        binding.newImage.setOnClickListener{
            startActivity(
                Intent(
                    this@MainActivity,
                    CameraActivity::class.java
                )
            )
        }

        binding.inboxImage.setOnClickListener{
            val intent = Intent(this@MainActivity, MessageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        binding.meImage.setOnClickListener{
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }



        binding.textView.bringToFront()
        binding.textView2.bringToFront()

    }




}