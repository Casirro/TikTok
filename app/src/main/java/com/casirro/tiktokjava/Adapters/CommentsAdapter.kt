package com.casirro.tiktokjava.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.casirro.tiktokjava.Activities.MainActivity
import com.casirro.tiktokjava.Adapters.CommentsAdapter.CommentsViewHolder
import com.casirro.tiktokjava.Modules.Comments
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.CommentsLayoutBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CommentsAdapter(var comments: List<Comments>, var context: Context) :
    RecyclerView.Adapter<CommentsViewHolder>() {

    private lateinit var database: FirebaseDatabase
    private lateinit var mainActivity: MainActivity



    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CommentsViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.comments_layout, viewGroup, false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val commentsAdapter = comments[position]
        holder.binding.textView54.text = commentsAdapter.comment
        mainActivity = MainActivity()
        database = FirebaseDatabase.getInstance()


        database.reference.child("users").child(commentsAdapter.senderId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                Glide.with(context).load(user?.profileImage).placeholder(R.drawable.avatar).into(holder.binding.profile)
                holder.binding.textView53.text = user?.username
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })











    }

    override fun getItemCount(): Int {
        return comments.size
    }

    inner class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: CommentsLayoutBinding

        init {
            binding = CommentsLayoutBinding.bind(itemView)


        }
    }
}