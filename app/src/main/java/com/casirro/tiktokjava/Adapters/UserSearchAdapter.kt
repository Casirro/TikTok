package com.casirro.tiktokjava.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.casirro.tiktokjava.Activities.ProfileActivity
import com.casirro.tiktokjava.Activities.UserProfileActivity
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.SearchUsersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserSearchAdapter(var context: Context, users: ArrayList<User>) :
    RecyclerView.Adapter<UserSearchAdapter.UserSearchViewHolder?>() {
    private lateinit var database: FirebaseDatabase
    var users: ArrayList<User>
    private var user1: User? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSearchViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.search_users, parent, false)
        return UserSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserSearchViewHolder, position: Int) {
        val user: User = users[position]

        holder.itemView.setOnClickListener {
        if (!user.uid.equals(FirebaseAuth.getInstance().uid)){
                val intent = Intent(context, UserProfileActivity::class.java)
                intent.putExtra("uid", user.uid)
                context.startActivity(intent)
        }else{

            context.startActivity(Intent(context, ProfileActivity::class.java))
        }


        }

        if (user.uid.equals(FirebaseAuth.getInstance().uid)){ holder.binding.view39.visibility = View.GONE; holder.binding.textView72.visibility = View.GONE}

        database.reference.child("users").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user1 = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        holder.binding.view39.setOnClickListener {


            if (holder.binding.view39.tag == "followed") {
                database.reference.child("following").child(FirebaseAuth.getInstance().uid!!)
                    .child(user.uid!!).removeValue()
                database.reference.child("followers").child(user.uid!!)
                    .child(FirebaseAuth.getInstance().uid!!).removeValue()
            } else {
                database.reference.child("following").child(FirebaseAuth.getInstance().uid!!)
                    .child(user.uid!!).setValue(user)
                database.reference.child("followers").child(user.uid!!)
                    .child(FirebaseAuth.getInstance().uid!!).setValue(user1)
            }

        }



        database.reference.child("following").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {



                    if (snapshot.child(user.uid!!).exists()){
                        holder.binding.view39.setBackgroundColor(Color.TRANSPARENT)
                        holder.binding.textView72.setTextColor(Color.BLACK)
                        holder.binding.textView72.text = "Sleduji"
                        holder.binding.view39.tag = "followed"

                    }else{
                        holder.binding.view39.setBackgroundColor(Color.rgb(255, 35, 79))
                        holder.binding.textView72.setTextColor(Color.WHITE)
                        holder.binding.textView72.text = "Sledovat"
                        holder.binding.view39.tag = "follow"
                    }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })








        database.reference.child("users").child(user.uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                holder.binding.username.text = user?.username
                holder.binding.textView54.text = user?.name
                Glide.with(context).load(user?.profileImage)
                    .placeholder(R.drawable.avatar)
                    .into(holder.binding.profile)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: SearchUsersBinding

        init {
            binding = SearchUsersBinding.bind(itemView)
            database = FirebaseDatabase.getInstance()
        }
    }

    init {
        this.users = users
    }
}