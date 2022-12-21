package com.casirro.tiktokjava.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.casirro.tiktokjava.Activities.ChatActivity
import com.casirro.tiktokjava.Activities.MessageActivity
import com.casirro.tiktokjava.Activities.UserProfileActivity
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.FriendsUsersBinding
import com.casirro.tiktokjava.databinding.SearchUsersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewChatUserAdapter(var context: Context, users: ArrayList<User>):
    RecyclerView.Adapter<NewChatUserAdapter.NewChatViewHolder?>() {
    private lateinit var database: FirebaseDatabase
    var users: ArrayList<User>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewChatUserAdapter.NewChatViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.friends_users, parent, false)
        return NewChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewChatUserAdapter.NewChatViewHolder, position: Int) {
        val user: User = users[position]

        holder.binding.name.text = user.name.toString()
        holder.binding.username.text = user.username.toString()
        Glide.with(context).load(user.profileImage).placeholder(R.drawable.avatar).into(holder.binding.profile)

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


    inner class NewChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: FriendsUsersBinding

        init {
            binding = FriendsUsersBinding.bind(itemView)
            database = FirebaseDatabase.getInstance()
        }
    }

    init {
        this.users = users
    }
}