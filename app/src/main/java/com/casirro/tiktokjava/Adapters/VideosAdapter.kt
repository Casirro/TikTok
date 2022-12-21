package com.casirro.tiktokjava.Adapters


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.casirro.chat.Models.Message
import com.casirro.tiktokjava.Activities.ChatActivity
import com.casirro.tiktokjava.Activities.CommentsActivity
import com.casirro.tiktokjava.Activities.ProfileActivity
import com.casirro.tiktokjava.Activities.UserProfileActivity
import com.casirro.tiktokjava.Adapters.VideosAdapter.VideosViewHolder
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.Modules.Video
import com.casirro.tiktokjava.databinding.VideosLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class VideosAdapter(private var videos: List<Video>, var context: Context) :
    RecyclerView.Adapter<VideosViewHolder>() {
    var name: TextView? = null
    private lateinit var database: FirebaseDatabase
    var auth: FirebaseAuth? = null
    var isPlaying = false
    var likesCount: Int? = null
    private var mViewHolder: VideosViewHolder? = null
    var user: User? = null
    private var users: MutableList<User> = ArrayList()
    private var shareAdapter: ShareAdapter? = null
    private lateinit var message: Message





    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): VideosViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(com.casirro.tiktokjava.R.layout.videos_layout, viewGroup, false)
        return VideosViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val videosAdapter = videos[position]
        var isUp = false
        val filePath = videosAdapter.videoPath.toString()







        database = FirebaseDatabase.getInstance()
        holder.binding.videoView.setVideoPath(videosAdapter.videoPath)
        holder.binding.videoView.setOnPreparedListener { mp ->
            mp.start()
            mp.isLooping = true
        }
        holder.binding.textView3.text = videosAdapter.username
        holder.binding.videoView.setOnCompletionListener { mp ->
            mp.start()
            isPlaying = true
        }
        holder.binding.videoView.setOnClickListener {
            if (!isUp) {
                isPlaying = if (isPlaying) {
                    holder.binding.videoView.pause()
                    holder.binding.imageView41.visibility = View.VISIBLE
                    false
                } else {
                    holder.binding.videoView.start()
                    holder.binding.imageView41.visibility = View.GONE
                    true
                }
            } else {
                slideDown(holder.binding.constraint)
                isUp = !isUp
            }

        }




        database.reference.child("users").child(videosAdapter.userId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                holder.binding.textView3.text = user?.name.toString()
                Glide.with(context).load(user?.profileImage).placeholder(com.casirro.tiktokjava.R.drawable.avatar).into(holder.binding.profile)
                holder.binding.textView3.setOnClickListener{
                    if (user!!.uid == FirebaseAuth.getInstance().uid){
                        val intent = Intent(context, UserProfileActivity::class.java)
                        intent.putExtra("name", user.username)
                        intent.putExtra("profileImage", user.profileImage)
                        intent.putExtra("uid", user.uid)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        holder.binding.imageView41.visibility = View.GONE
                    }else{
                        context.startActivity(Intent(context,  ProfileActivity::class.java))
                    }

                }
                holder.binding.profile.setOnClickListener{
                    if (user!!.uid == FirebaseAuth.getInstance().uid){
                        val intent = Intent(context, UserProfileActivity::class.java)
                        intent.putExtra("name", user.username)
                        intent.putExtra("profileImage", user.profileImage)
                        intent.putExtra("uid", user.uid)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        holder.binding.imageView41.visibility = View.GONE
                    }else{
                        context.startActivity(Intent(context,  ProfileActivity::class.java))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        database.reference.child("videos").child(videosAdapter.userId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                likesCount = snapshot.child("likesCount").getValue(Int::class.java)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



        val video = Video(videosAdapter.videoPath, videosAdapter.caption, videosAdapter.videoId, videosAdapter.userId)
        holder.binding.imageView42.setOnClickListener {
            if (holder.binding.imageView42.tag === "like") {
                database.reference.child("likes").child(videosAdapter.videoId!!).child(
                    FirebaseAuth.getInstance().uid!!
                ).setValue(true)

                database.reference.child("usersLikes").child(FirebaseAuth.getInstance().uid!!).child(videosAdapter.videoId!!).setValue(video)
            } else {
                database.reference.child("likes").child(videosAdapter.videoId!!).child(
                    FirebaseAuth.getInstance().uid!!
                ).removeValue()
                database.reference.child("usersLikes").child(FirebaseAuth.getInstance().uid!!).child(videosAdapter.videoId!!).removeValue()

            }
        }
        database.reference.child("likes").child(videosAdapter.videoId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    holder.binding.textView50.text = snapshot.childrenCount.toString()
                    if (snapshot.child(FirebaseAuth.getInstance().uid!!).exists()) {
                        holder.binding.imageView42.setImageResource(com.casirro.tiktokjava.R.drawable.ic_action_name)
                        holder.binding.imageView42.tag = "liked"
                    } else {
                        holder.binding.imageView42.setImageResource(com.casirro.tiktokjava.R.drawable.heart_icon)
                        holder.binding.imageView42.tag = "like"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        holder.binding.textView3.text = videosAdapter.username
        holder.binding.textView4.text = videosAdapter.caption

        holder.binding.imageView43.setOnClickListener {
            val intent = Intent(this.context, CommentsActivity::class.java)
            intent.putExtra("videoUid", videosAdapter.videoId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            holder.binding.imageView41.visibility = View.GONE

        }
        database.reference.child("comments").child(videosAdapter.videoId.toString()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                    holder.binding.textView51.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })




        holder.binding.imageView44.setOnClickListener {
            slideUp(holder.binding.constraint)
            isUp = true
        }

        holder.binding.imageView59.setOnClickListener {
            slideDown(holder.binding.constraint)
            isUp = !isUp
        }

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        holder.binding.shimmerRecyclerView.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.HORIZONTAL
        shareAdapter = ShareAdapter(users, context)
        holder.binding.shimmerRecyclerView.adapter = shareAdapter









        database.reference.child("following").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (dataSnapshot in snapshot.children){
                    val user = dataSnapshot.getValue(User::class.java)
                    users.add(user!!)
                }
                shareAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    override fun getItemViewType(position: Int): Int {
        // Return the itemView for the given position
        return position
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





    override fun getItemCount(): Int {
        return videos.size
    }


    class VideosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var constraintLayout: ConstraintLayout
        var binding: VideosLayoutBinding

        init {
            binding = VideosLayoutBinding.bind(itemView)
            binding.textView3.bringToFront()
            constraintLayout = binding.constraintLayout
            binding.textView4.bringToFront()
            binding.imageView41.bringToFront()
            binding.imageView42.bringToFront()
            binding.imageView43.bringToFront()
            binding.imageView44.bringToFront()
            binding.imageView45.bringToFront()
        }
    }

}