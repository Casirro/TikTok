package com.casirro.tiktokjava.Activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.casirro.chat.Adapters.MessagesAdapter
import com.casirro.chat.Models.Message
import com.casirro.tiktokjava.Adapters.ChatUserAdapter
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    var adapter: MessagesAdapter? = null
    var usersAdapter: ChatUserAdapter? = null
    lateinit var messages: ArrayList<Message>
    lateinit var users: ArrayList<User>
    var recyclerView: RecyclerView? = null
    var senderRoom: String? = null
    var receiverRoom: String? = null
    private lateinit var database: FirebaseDatabase
    var storage: FirebaseStorage? = null
    var dialog: ProgressDialog? = null
    var senderUid: String? = null
    var receiverUid: String? = null
    var token: String? = null
    var name: String? = null
    var user: User? = null
    var context: Context? = null
    var seenListener: ValueEventListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Nahrávání obrázku...")
        dialog!!.setCancelable(false)
        messages = ArrayList()
        users = ArrayList()
        val name = intent.getStringExtra("name")
        val profile = intent.getStringExtra("profileImage")
        val file = intent.getStringExtra("file")
        val token = intent.getStringExtra("token")

        //Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
        binding.name.text = name
        Glide.with(this@ChatActivity).load(profile)
            .placeholder(R.drawable.avatar)
            .into(binding.profile)
        binding.imageView2.setOnClickListener { finish() }
        receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().uid
        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid
        adapter = MessagesAdapter(this, messages, senderRoom!!, receiverRoom!!)
        usersAdapter = ChatUserAdapter(applicationContext, users)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        database.reference.child("chats")
            .child(senderRoom!!)
            .child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    for (snapshot1 in snapshot.children) {
                        val message = snapshot1.getValue(
                            Message::class.java
                        )
                        message!!.messageId = snapshot1.key
                        messages.add(message)
                        MakeFriend()
                        MakeFriend2()
                        binding.recyclerView.scrollToPosition(messages.size - 1)
                    }
                    adapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        if(file != null){
            val filePath = intent.getStringExtra("file")
            val messageTxt: String =
                binding.messageBox.text.toString()
            val date = Date()
            val message = Message(
                messageTxt,
                senderUid,
                filePath,
                date.time
            )
            message.message = "photo"
            message.imageUrl = filePath
            binding.messageBox.setText("")
            val randomKey = database!!.reference.push().key
            val lastMsgObj = HashMap<String, Any?>()
            lastMsgObj["lastMsg"] = message.message
            lastMsgObj["lastMsgTime"] = date.time
            database!!.reference.child("chats").child(senderRoom!!)
                .updateChildren(lastMsgObj)
            database!!.reference.child("chats").child(receiverRoom!!)
                .updateChildren(lastMsgObj)
            database!!.reference.child("chats")
                .child(senderRoom!!)
                .child("messages")
                .child(randomKey!!)
                .setValue(message).addOnSuccessListener {
                    database!!.reference.child("chats")
                        .child(receiverRoom!!)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener { }
                }
        }



        binding.profile.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("uid", receiverUid)

            startActivity(intent)
        }
        binding.sendBtn.setOnClickListener {
            if (binding.messageBox.text.toString().isNotEmpty()) {
                val messageTxt: String = binding.messageBox.text.toString()
                val date = Date()
                val message = Message(messageTxt, senderUid, date.time)
                binding.messageBox.setText("")
                binding.recyclerView.scrollToPosition(messages!!.size - 1)
                val randomKey = database!!.reference.push().key
                val lastMsgObj = HashMap<String, Any?>()
                lastMsgObj["lastMsg"] = message.message
                lastMsgObj["lastMsgTime"] = date.time
                lastMsgObj["receiverId"] = receiverUid
                database!!.reference.child("chats").child(senderRoom!!).updateChildren(lastMsgObj)
                database!!.reference.child("chats").child(receiverRoom!!).updateChildren(lastMsgObj)
                database!!.reference.child("chats")
                    .child(senderRoom!!)
                    .child("messages")
                    .child(randomKey!!)
                    .setValue(message).addOnSuccessListener {
                        database!!.reference.child("chats")
                            .child(receiverRoom!!)
                            .child("messages")
                            .child(randomKey)
                            .setValue(message).addOnSuccessListener {
                                sendNotification(
                                    name,
                                    message.message,
                                    token
                                )
                            }
                    }
            }
        }
        val handler = Handler()
        binding.messageBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                database!!.reference.child("presence").child(senderUid!!).setValue("píše...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping, 1000)
            }

            var userStoppedTyping =
                Runnable {
                    database!!.reference.child("presence").child(senderUid!!).setValue("Online")
                }
        })
        supportActionBar!!.setDisplayShowTitleEnabled(false)


//        getSupportActionBar().setTitle(name);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private fun sendNotification(name: String?, message: String?, token: String?) {
        try {
            val queue = Volley.newRequestQueue(this)
            val url = "https://fcm.googleapis.com/fcm/send"
            val data = JSONObject()
            data.put("title", name)
            data.put("body", message)
            val notificationData = JSONObject()
            notificationData.put("notification", data)
            notificationData.put("to", token)
            val request: JsonObjectRequest =
                object : JsonObjectRequest(url, notificationData, Response.Listener {
                    // Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
                }, Response.ErrorListener { }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val map = HashMap<String, String>()
                        val key =
                            "Key=AAAASn2Fs4A:APA91bGdTVxFBP-V0NN_zLjQTUb7yr9Shy0sYcSN2MvHxTksz11FktDxUt44hKD3CyD2ghCX61RGJW25F0mBPpTBrSArmo9emaKP8HqRQGe5A8vrdygKbY-Kfph9YvaeQnPmif5a1Zr7"
                        map["Content-Type"] = "application/json"
                        map["Authorization"] = key
                        return map
                    }
                }
            queue.add(request)
        } catch (ex: Exception) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25) {
            if (data != null) {
                if (data.data != null) {
                    val selectedImage = data.data
                    val calendar = Calendar.getInstance()
                    val reference = storage!!.reference.child("chats")
                        .child(calendar.timeInMillis.toString() + "")
                    dialog!!.show()
                    reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                        dialog!!.dismiss()
                        if (task.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener { uri ->
                                val filePath = intent.getStringExtra("file")
                                val messageTxt: String =
                                    binding.messageBox.text.toString()
                                val date = Date()
                                val message = Message(
                                    messageTxt,
                                    senderUid,
                                    date.time
                                )
                                message.message = "photo"
                                message.imageUrl = filePath
                                binding.messageBox.setText("")
                                val randomKey = database!!.reference.push().key
                                val lastMsgObj = HashMap<String, Any?>()
                                lastMsgObj["lastMsg"] = message.message
                                lastMsgObj["lastMsgTime"] = date.time
                                database!!.reference.child("chats").child(senderRoom!!)
                                    .updateChildren(lastMsgObj)
                                database!!.reference.child("chats").child(receiverRoom!!)
                                    .updateChildren(lastMsgObj)
                                database!!.reference.child("chats")
                                    .child(senderRoom!!)
                                    .child("messages")
                                    .child(randomKey!!)
                                    .setValue(message).addOnSuccessListener {
                                        database!!.reference.child("chats")
                                            .child(receiverRoom!!)
                                            .child("messages")
                                            .child(randomKey)
                                            .setValue(message).addOnSuccessListener { }
                                    }

                                //Toast.makeText(ChatActivity.this, filePath, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("presence").child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database.reference.child("presence").child(currentId!!).setValue("Offline")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.usrProfile -> database.reference.child("users").child(receiverUid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user: User? = snapshot.getValue(User::class.java)
                        val intent = Intent(applicationContext, UserProfileActivity::class.java)
                        intent.putExtra("name", user?.name)
                        intent.putExtra("profileImage", user?.profileImage)
                        intent.putExtra("uid", user?.uid)
                        startActivity(intent)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
        return super.onOptionsItemSelected(item)
    }
    private fun MakeFriend(){
        database.reference.child("users").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val friend = snapshot.getValue(User::class.java)
                database.reference.child("usersFriends").child(receiverUid!!)
                        .child(FirebaseAuth.getInstance().uid!!).setValue(friend)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun MakeFriend2(){
        database.reference.child("users").child(receiverUid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val friend = snapshot.getValue(User::class.java)
                database.reference.child("usersFriends").child(FirebaseAuth.getInstance().uid!!)
                    .child(receiverUid!!).setValue(friend)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}