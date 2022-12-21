package com.casirro.tiktokjava.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.casirro.tiktokjava.Adapters.MyVideosAdapter
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.Modules.Video
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.ActivityProfileBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var layoutManager: RecyclerView.LayoutManager? = null
    var hide = true
    private var myVideosAdapter: MyVideosAdapter? = null
    private val videos: MutableList<Video> = ArrayList()
    private val videos1: MutableList<Video> = ArrayList()
    private lateinit var database: FirebaseDatabase
    private lateinit var followedUsers: FollowedUsers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        database = FirebaseDatabase.getInstance()

        followedUsers = FollowedUsers()


        binding.imageView9.setOnClickListener {
            val intent = Intent(this@ProfileActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        binding.settinsImage.setOnClickListener {
            val intent = Intent(this@ProfileActivity, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        database.reference.child("users").child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(
                        User::class.java
                    )
                    binding.textView13.text = user?.bio
                    binding.textView5.text = user?.name
                    binding.textView6.text = user?.username
                    Glide.with(applicationContext).load(user?.profileImage)
                        .placeholder(R.drawable.avatar).into(
                        binding.imageView
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        database.reference.child("videos").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val likesCount = snapshot.child("likesCount").getValue(Int::class.java)
                binding.textView8.text = likesCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.newImage.setOnClickListener {
            startActivity(
                Intent(
                    this@ProfileActivity,
                    CameraActivity::class.java
                )
            )
        }
        binding.discoverImage.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        binding.inboxImage.setOnClickListener{
            val intent = Intent(this, MessageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        binding.imageView6.setOnClickListener {
            intent = Intent(this, EditProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)

        }
        layoutManager = GridLayoutManager(applicationContext, 3)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.isNestedScrollingEnabled = false
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        myVideosAdapter = MyVideosAdapter(videos, applicationContext)
        binding.recyclerView.adapter = myVideosAdapter


        binding.textView13.setOnClickListener {
            intent = Intent(this, BioActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)

        }


        binding.textView9.setOnClickListener {
            val intent = Intent(this, FollowedUsers::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("haf", "haf")
            startActivity(intent)
        }
        binding.textView7.setOnClickListener {
            val intent = Intent(this, FollowedUsers::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("zabak", "zabak")
            startActivity(intent)
        }
        myVideos()


        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        myVideos()



                    }
                    1 -> {
                        likedVideos()



                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {


            }

        })
        database.reference.child("followers").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textView7.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        database.reference.child("following").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textView9.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })




    }
    private fun myVideos(){
        database.reference.child("videos").child(FirebaseAuth.getInstance().uid!!).child("userVideos")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    videos.clear()
                    for (snapshot1 in snapshot.children){
                        val video = snapshot1.getValue(Video::class.java)
                        videos.add(video!!)
                    }
                    myVideosAdapter?.notifyDataSetChanged()

                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
    private fun likedVideos() {
        database.reference.child("usersLikes").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                videos.clear()
                for (snapshot1 in snapshot.children){
                    val video = snapshot1.getValue(Video::class.java)
                    videos.add(video!!)
                }


                myVideosAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


}