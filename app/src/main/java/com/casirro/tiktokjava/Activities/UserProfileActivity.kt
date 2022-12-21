package com.casirro.tiktokjava.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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
import com.casirro.tiktokjava.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private var layoutManager: RecyclerView.LayoutManager? = null
    var hide = true
    private var myVideosAdapter: MyVideosAdapter? = null
    private val videos: MutableList<Video> = ArrayList()
    private lateinit var database: FirebaseDatabase
    private var user: User? = null
    private var user1: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        database = FirebaseDatabase.getInstance()

        val uid = intent.getStringExtra("uid")
        database.reference.child("users").child(uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    binding.textView13.text = user?.bio
                    binding.textView5.text = user?.name.toString()
                    binding.textView6.text = user?.name.toString()
                    Glide.with(applicationContext).load(user?.profileImage).placeholder(R.drawable.avatar).into(binding.imageView)



                }

                override fun onCancelled(error: DatabaseError) {}
            })
        database.reference.child("videos").child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val likesCount = snapshot.child("likesCount").getValue(Int::class.java)
                binding.textView8.text = likesCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        binding.imageView54.setOnClickListener { finish() }




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

        database.reference.child("users").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        database.reference.child("users").child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user1 = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        database.reference.child("videos").child(uid).child("userVideos")
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

        binding.imageView6.setOnClickListener {
            if (binding.imageView6.tag == "follow") {
                database.reference.child("following").child(FirebaseAuth.getInstance().uid!!)
                    .child(uid).setValue(user1)
                database.reference.child("followers").child(uid)
                    .child(FirebaseAuth.getInstance().uid!!).setValue(user)
            } else {
                database.reference.child("following").child(FirebaseAuth.getInstance().uid!!)
                    .child(uid).removeValue()
                database.reference.child("followers").child(uid)
                    .child(FirebaseAuth.getInstance().uid!!).removeValue()
            }

        }
        database.reference.child("following").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(uid).exists()){
                    binding.imageView6.setColorFilter(Color.TRANSPARENT)
                    binding.textView69.setTextColor(Color.BLACK)
                    binding.textView69.text = "Sleduji"
                    binding.imageView6.tag = "followed"
                }else{
                    binding.imageView6.setBackgroundColor(Color.rgb(234, 67, 89))
                    binding.textView69.setTextColor(Color.WHITE)
                    binding.textView69.text = "Sledovat"
                    binding.imageView6.tag = "follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        database.reference.child("followers").child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textView7.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        database.reference.child("following").child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textView9.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



    }


}