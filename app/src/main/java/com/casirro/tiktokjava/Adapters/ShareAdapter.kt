package com.casirro.tiktokjava.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.casirro.chat.Models.Message
import com.casirro.tiktokjava.Activities.ChatActivity
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.Modules.Video
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.ChatShareVideoBinding
import com.casirro.tiktokjava.databinding.MyVideosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ShareAdapter(var users: List<User>, var context: Context):
    RecyclerView.Adapter<ShareAdapter.ShareViewHolder>() {
    lateinit var database: FirebaseDatabase
    private var mViewHolder: ShareViewHolder? = null
    private var filePath: String? = null
    private val videos: MutableList<Video> = ArrayList()
    var videosAdapter = VideosAdapter(videos, context)


    override fun onCreateViewHolder(group: ViewGroup, viewType: Int): ShareViewHolder {
        val view = LayoutInflater.from(group.context).inflate(R.layout.chat_share_video, group, false)
        return ShareViewHolder(view)

    }

    override fun getItemViewType(position: Int): Int {
        // Return the itemView for the given position
        return position
    }


    override fun onBindViewHolder(holder: ShareViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val user = users[position]
        database = FirebaseDatabase.getInstance()
        val receiverUid = user.uid
        val senderUid = FirebaseAuth.getInstance().uid
        val senderRoom = senderUid + receiverUid
        val receiverRoom = receiverUid + senderUid
        val haf = videosAdapter.getItemViewType(position)



        Glide.with(context).load(user.profileImage).placeholder(R.drawable.avatar).into(holder.binding.profile)
        holder.binding.username.text = user.name
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("name", user.username)
            intent.putExtra("profileImage", user.profileImage)
            intent.putExtra("uid", user.uid)
            intent.putExtra("file", filePath)


            context.startActivity(intent)
        }










    }


    override fun getItemCount(): Int {
        return users.size

    }

    inner class ShareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var binding: ChatShareVideoBinding



        init {
            binding = ChatShareVideoBinding.bind(itemView)


        }
    }

}