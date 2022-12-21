package com.casirro.tiktokjava.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.Modules.Video
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.ActivityVideoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    private lateinit var database: FirebaseDatabase
    private var isPlaying = false
    var isUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        database = FirebaseDatabase.getInstance()

        val constraint = binding.constraint

        val videoPath = intent.getStringExtra("videoPath")
        val videoUid = intent.getStringExtra("videoUid")
        val caption = intent.getStringExtra("caption")
        val uid = intent.getStringExtra("uid")
        val video = Video(videoPath, caption, videoUid, uid)

        binding.videoView.setVideoPath(videoPath)
        binding.videoView.setOnPreparedListener { mp ->
            mp.start()
            mp.isLooping = true
        }



        database.reference.child("users").child(uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.textView3.text = user?.username.toString()
                Glide.with(applicationContext).load(user?.profileImage).placeholder(R.drawable.avatar).into(binding.profile)
            }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        binding.imageView42.setOnClickListener {
            if (binding.imageView42.tag === "like") {
                database.reference.child("likes").child(videoUid!!).child(
                    FirebaseAuth.getInstance().uid!!
                ).setValue(true)

                database.reference.child("usersLikes").child(FirebaseAuth.getInstance().uid!!).child(videoUid).setValue(video)
            } else {
                database.reference.child("likes").child(videoUid!!).child(
                    FirebaseAuth.getInstance().uid!!
                ).removeValue()
                database.reference.child("usersLikes").child(FirebaseAuth.getInstance().uid!!).child(videoUid).removeValue()
            }
        }
        database.reference.child("likes").child(videoUid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.textView50.text = snapshot.childrenCount.toString()
                    if (snapshot.child(FirebaseAuth.getInstance().uid!!).exists()) {
                        binding.imageView42.setImageResource(R.drawable.ic_action_name)
                        binding.imageView42.tag = "liked"
                    } else {
                        binding.imageView42.setImageResource(R.drawable.heart_icon)
                        binding.imageView42.tag = "like"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })


        database.reference.child("comments").child(videoUid.toString()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textView51.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.textView4.text = caption.toString()
        binding.videoView.setOnCompletionListener { mp ->
            mp.start()
            isPlaying = true
        }
        binding.videoView.setOnClickListener {
            if (!isUp){
                isPlaying = if (isPlaying) {
                    binding.videoView.pause()
                    binding.imageView41.visibility = View.VISIBLE
                    false
                } else {
                    binding.videoView.start()
                    binding.imageView41.visibility = View.GONE
                    true
                }
            } else {
            slideDown(binding.constraint)
            isUp = !isUp
        }

        }

        binding.imageView44.setOnClickListener {
            slideUp(binding.constraint)
            isUp = true
        }

        binding.imageView59.setOnClickListener {
            slideDown(binding.constraint)
            isUp = !isUp
        }

        binding.imageView43.setOnClickListener {
            val intent = Intent(applicationContext, CommentsActivity::class.java)
            intent.putExtra("videoUid", videoUid)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
        }

        binding.imageView7.setOnClickListener {
            finish()
        }


    }

    private fun slideUp(constraint: ConstraintLayout){
        constraint.bringToFront()
        constraint.visibility = View.VISIBLE
        val animate = TranslateAnimation(0F, 0F, constraint.height.toFloat(), 0F)
        animate.duration = 500
        animate.fillAfter = true
        constraint.startAnimation(animate)
    }


    // slide the view from its current position to below itself
    private fun slideDown(constraint: ConstraintLayout) {
        val animate = TranslateAnimation(
            0F,  // fromXDelta
            0F,  // toXDelta
            0F,  // fromYDelta
            constraint.height.toFloat()
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true

        constraint.startAnimation(animate)
        constraint.visibility = View.GONE
    }
}