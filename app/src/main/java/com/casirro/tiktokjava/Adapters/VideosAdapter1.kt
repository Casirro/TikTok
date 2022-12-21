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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.casirro.tiktokjava.Activities.CommentsActivity
import com.casirro.tiktokjava.Activities.UserProfileActivity
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.casirro.chat.Adapters.MessagesAdapter
import com.casirro.tiktokjava.Fragments.ShareFragment
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.Modules.Video
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.ChatShareVideoBinding
import com.casirro.tiktokjava.databinding.VideosLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class VideosAdapter1(var videos: List<Video>, var users: List<User>, context: Context) :
    RecyclerView.Adapter<ViewHolder>() {
    var context: Context
    var name: TextView? = null
    private lateinit var database: FirebaseDatabase
    var auth: FirebaseAuth? = null
    var isPlaying = false
    var likesCount: Int? = null
    var isUp = false
    private val VIEW_TYPE_ONE = 1
    private val VIEW_TYPE_TWO = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return if (viewType == VIEW_TYPE_ONE) {
            val view: View =
                LayoutInflater.from(context)
                    .inflate(R.layout.videos_layout, parent, false)
            VideosViewHolder1(view)
        } else {
            val view: View =
                LayoutInflater.from(context)
                    .inflate(R.layout.chat_share_video, parent, false)
            VideosViewHolder(view)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (!isUp) {
            VIEW_TYPE_ONE
        } else {
            VIEW_TYPE_TWO
        }
    }


    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.javaClass.equals(VIEW_TYPE_ONE::class.java)){
            val videosAdapter = videos[position]
            val viewHolder = holder as VideosViewHolder1
            database = FirebaseDatabase.getInstance()
            viewHolder.binding.videoView.setVideoPath(videosAdapter.videoPath)
            viewHolder.binding.videoView.setOnPreparedListener { mp ->
                mp.start()
                mp.isLooping = true
            }
            viewHolder.binding.textView3.text = videosAdapter.username
            viewHolder.binding.videoView.setOnCompletionListener { mp ->
                mp.start()
                isPlaying = true
            }
            viewHolder.binding.videoView.setOnClickListener {
                if (!isUp) {
                    isPlaying = if (isPlaying) {
                        viewHolder.binding.videoView.pause()
                        viewHolder.binding.imageView41.visibility = View.VISIBLE
                        false
                    } else {
                        viewHolder.binding.videoView.start()
                        viewHolder.binding.imageView41.visibility = View.GONE
                        true
                    }
                } else {
                    slideDown(viewHolder.binding.constraint)
                    isUp = !isUp
                }

            }




            database.reference.child("users").child(videosAdapter.userId!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        viewHolder.binding.textView3.text = user?.name.toString()
                        Glide.with(context).load(user?.profileImage)
                            .placeholder(com.casirro.tiktokjava.R.drawable.avatar)
                            .into(viewHolder.binding.profile)
                        viewHolder.binding.textView3.setOnClickListener {
                            val intent = Intent(context, UserProfileActivity::class.java)
                            intent.putExtra("name", user?.username)
                            intent.putExtra("profileImage", user?.profileImage)
                            intent.putExtra("uid", user?.uid)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            viewHolder.binding.imageView41.visibility = View.GONE
                        }
                        viewHolder.binding.profile.setOnClickListener {
                            val intent = Intent(context, UserProfileActivity::class.java)
                            intent.putExtra("name", user?.username)
                            intent.putExtra("profileImage", user?.profileImage)
                            intent.putExtra("uid", user?.uid)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            viewHolder.binding.imageView41.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            database.reference.child("videos").child(videosAdapter.userId!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        likesCount = snapshot.child("likesCount").getValue(Int::class.java)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })


            val video = Video(
                videosAdapter.videoPath,
                videosAdapter.caption,
                videosAdapter.videoId,
                videosAdapter.userId
            )
            viewHolder.binding.imageView42.setOnClickListener {
                if (viewHolder.binding.imageView42.tag === "like") {
                    database.reference.child("likes").child(videosAdapter.videoId!!).child(
                        FirebaseAuth.getInstance().uid!!
                    ).setValue(true)

                    database.reference.child("usersLikes").child(FirebaseAuth.getInstance().uid!!)
                        .child(videosAdapter.videoId!!).setValue(video)
                } else {
                    database.reference.child("likes").child(videosAdapter.videoId!!).child(
                        FirebaseAuth.getInstance().uid!!
                    ).removeValue()
                    database.reference.child("usersLikes").child(FirebaseAuth.getInstance().uid!!)
                        .child(videosAdapter.videoId!!).removeValue()

                }
            }
            database.reference.child("likes").child(videosAdapter.videoId!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        viewHolder.binding.textView50.text = snapshot.childrenCount.toString()
                        if (snapshot.child(FirebaseAuth.getInstance().uid!!).exists()) {
                            viewHolder.binding.imageView42.setImageResource(com.casirro.tiktokjava.R.drawable.ic_action_name)
                            viewHolder.binding.imageView42.tag = "liked"
                        } else {
                            viewHolder.binding.imageView42.setImageResource(com.casirro.tiktokjava.R.drawable.heart_icon)
                            viewHolder.binding.imageView42.tag = "like"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            viewHolder.binding.textView3.text = videosAdapter.username
            viewHolder.binding.textView4.text = videosAdapter.caption

            viewHolder.binding.imageView43.setOnClickListener {
                val intent = Intent(this.context, CommentsActivity::class.java)
                intent.putExtra("videoUid", videosAdapter.videoId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                viewHolder.binding.imageView41.visibility = View.GONE

            }
            database.reference.child("comments").child(videosAdapter.videoId.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        viewHolder.binding.textView51.text = snapshot.childrenCount.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })




            viewHolder.binding.imageView44.setOnClickListener {
                slideUp(viewHolder.binding.constraint)
            }

            viewHolder.binding.imageView59.setOnClickListener {
                slideDown(viewHolder.binding.constraint)
                isUp = !isUp
            }


        }else if (holder.javaClass.equals(VIEW_TYPE_TWO::class.java)){
            val user = users[position]

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
    }




    override fun getItemCount(): Int {
        return videos.size

    }


    inner class VideosViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    inner class VideosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ChatShareVideoBinding

        init {
            binding = ChatShareVideoBinding.bind(itemView)
        }

    }
    init {
        this.context = context
    }

}


