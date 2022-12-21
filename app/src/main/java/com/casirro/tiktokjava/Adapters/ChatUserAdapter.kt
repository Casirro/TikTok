package com.casirro.tiktokjava.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.casirro.tiktokjava.Activities.ChatActivity
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.UserMessageLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ChatUserAdapter(var context: Context, users: ArrayList<User>):
    RecyclerView.Adapter<ChatUserAdapter.ChatViewHolder?>() {
    private lateinit var database: FirebaseDatabase
    var users: ArrayList<User>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserAdapter.ChatViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_message_layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatUserAdapter.ChatViewHolder, position: Int) {
        val user: User = users[position]

        holder.binding.username.text = user.name.toString()
        Glide.with(context).load(user.profileImage).placeholder(R.drawable.avatar).into(holder.binding.profile)

        val senderId = FirebaseAuth.getInstance().uid

        val senderRoom = senderId + user.uid

        FirebaseDatabase.getInstance().reference
            .child("chats")
            .child(senderRoom)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val lastMsg = snapshot.child("lastMsg").getValue(String::class.java)
                        val time = snapshot.child("lastMsgTime").getValue(Long::class.java)!!!!
                        val dateFormat = SimpleDateFormat("hh:mm a")
                        holder.binding.msgTime.text = dateFormat.format(Date(time))
                        holder.binding.lastMsg.text = lastMsg
                    } else {
                        holder.binding.lastMsg.text = "Tap to chat"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", user.username)
            intent.putExtra("profileImage", user.profileImage)
            intent.putExtra("uid", user.uid)
            context.startActivity(intent)

        }


    }

    override fun getItemCount(): Int {
        return users.size
    }


    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: UserMessageLayoutBinding

        init {
            binding = UserMessageLayoutBinding.bind(itemView)
            database = FirebaseDatabase.getInstance()
        }
    }

    init {
        this.users = users
    }
}