package com.casirro.tiktokjava.Activities

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.casirro.tiktokjava.Adapters.CommentsAdapter
import com.casirro.tiktokjava.Adapters.SoftInputAssist
import com.casirro.tiktokjava.Modules.Comments
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.databinding.ActivityCommentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var softInputAssist: SoftInputAssist
    private var user: User? = null
    var commentsAdapter: CommentsAdapter? = null
    private val comments: MutableList<Comments> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()








        database.reference.child("users").child(FirebaseAuth.getInstance().uid.toString()).addValueEventListener(object : ValueEventListener{
    override fun onDataChange(snapshot: DataSnapshot) {
        user = snapshot.getValue(User::class.java)

    }

    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }

})

        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()
        val videoId: String? = intent.getStringExtra("videoUid")

        binding.imageView46.setOnClickListener {
            val comment: String = binding.editTextTextMultiLine.text.toString()
            val senderId: String = FirebaseAuth.getInstance().uid.toString()
            val commentId: String = database.reference.push().key!!

            val newComment =
                Comments(commentId, senderId, videoId, comment, user?.username, user?.profileImage)
            database.reference.child("comments").child(videoId.toString()).child(commentId)
                .setValue(newComment)
            binding.editTextTextMultiLine.setText("")
            binding.commentsRecycler.scrollToPosition(comments.size - 1)


        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.commentsRecycler.layoutManager = layoutManager
        commentsAdapter = CommentsAdapter(comments, applicationContext)
        binding.commentsRecycler.adapter = commentsAdapter

        database.reference.child("comments").child(videoId.toString())
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        comments.clear()
                        for (snapshot1 in snapshot.children) {
                            val comment = snapshot1.getValue(Comments::class.java)
                            comments.add(comment!!)

                        }
                        commentsAdapter!!.notifyDataSetChanged()
                    }

                }

                override fun onCancelled(error: DatabaseError) {}
            })

        binding.imageView49.setOnClickListener {
            finish()
        }

        binding.editTextTextMultiLine.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (binding.editTextTextMultiLine.text.isNotEmpty()){
                    binding.imageView46.visibility = View.VISIBLE
                }else{
                    binding.imageView46.visibility = View.GONE
                }




            }

        })

    }

}