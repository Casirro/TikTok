package com.casirro.tiktokjava.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.casirro.tiktokjava.Adapters.SoftInputAssist
import com.casirro.tiktokjava.Adapters.UserSearchAdapter
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.databinding.ActivityFollowedUsersBinding
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FollowedUsers : AppCompatActivity() {
    private var followersUid: User? = null
    var binding: ActivityFollowedUsersBinding? = null
    var database: FirebaseDatabase? = null
    private lateinit var users: ArrayList<User>
    private var followingUid: User? = null
    private lateinit var softInputAssist: SoftInputAssist
    var usersProfileAdapter: UserSearchAdapter? = null

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowedUsersBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()

        val haf = intent.getStringExtra("haf")
        val zabak = intent.getStringExtra("zabak")




        users = ArrayList()
        usersProfileAdapter = UserSearchAdapter(this, users)
        binding!!.recyclerView.layoutManager = LinearLayoutManager(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        binding!!.recyclerView.adapter = usersProfileAdapter

        database!!.reference.child("following").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for (snapshot1 in snapshot.children){
                    followingUid = snapshot1.getValue(User::class.java)

                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        database!!.reference.child("users").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding!!.textView71.text = snapshot.child("name").getValue(String::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        database!!.reference.child("followers").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for (snapshot1 in snapshot.children){
                    followersUid = snapshot1.getValue(User::class.java)

                }




            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })





        binding!!.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        followers()





                    }
                    1 -> {
                        intent.getStringExtra("haf")
                        following()

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {


            }

        })

        if (zabak == "zabak"){

            following()

        }else{
            binding!!.tab.getChildAt(1).isActivated
            followers()
        }
        binding!!.imageView55.setOnClickListener{ finish() }




    }
    fun followers(){
        database!!.reference.child("following").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (snapshot1 in snapshot.children){
                    val user = snapshot1.getValue(User::class.java)





                    users.add(user!!)
                }

                usersProfileAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun following(){
        database!!.reference.child("followers").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (snapshot1 in snapshot.children){
                    val user = snapshot1.getValue(User::class.java)



                    users.add(user!!)
                }

                usersProfileAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}