package com.casirro.chat.Adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.casirro.chat.Models.Message
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.DeleteDialogBinding
import com.casirro.tiktokjava.databinding.ItemReceiveBinding
import com.casirro.tiktokjava.databinding.ItemSentBinding
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.ReactionsConfigBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class MessagesAdapter(
    context: Context,
    messages: ArrayList<Message>,
    senderRoom: String,
    receiverRoom: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var context: Context
    private var messages: ArrayList<Message>
    var database: FirebaseDatabase? = null
    private val ITEM_SENT = 1
    private val ITEM_RECEIVE = 2
    var senderRoom: String
    var receiverRoom: String
    var adapter: MessagesAdapter? = null
    var remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false)
            SentViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (FirebaseAuth.getInstance().uid == message.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val reactions = intArrayOf(
            R.drawable.ic_fb_like,
            R.drawable.ic_fb_love,
            R.drawable.ic_fb_laugh,
            R.drawable.ic_fb_wow,
            R.drawable.ic_fb_sad,
            R.drawable.ic_fb_angry
        )
        val config = ReactionsConfigBuilder(context)
            .withReactions(reactions)
            .build()
        val popup = ReactionPopup(context, config) label@{ pos: Int ->
            if (pos < 0) return@label false
            if (holder.javaClass == SentViewHolder::class.java) {
                val viewHolder =
                    holder as SentViewHolder
                viewHolder.binding.feeling.setImageResource(reactions[pos])
                viewHolder.binding.feeling.visibility = View.VISIBLE
            } else {
                val viewHolder =
                    holder as ReceiverViewHolder
                viewHolder.binding.feeling.setImageResource(reactions[pos])
                viewHolder.binding.feeling.visibility = View.VISIBLE
            }
            message.feeling = pos
            FirebaseDatabase.getInstance().reference
                .child("chats")
                .child(senderRoom)
                .child("messages")
                .child(message.messageId!!).setValue(message)
            FirebaseDatabase.getInstance().reference
                .child("chats")
                .child(receiverRoom)
                .child("messages")
                .child(message.messageId!!).setValue(message)
            true // true is closing popup, false is requesting a new selection
        }
        if (holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            if (message.message == "photo") {
                viewHolder.binding.image.visibility = View.VISIBLE
                viewHolder.binding.message.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.binding.image)
            }
            viewHolder.binding.message.text = message.message
            if (message.feeling >= 0) {
                viewHolder.binding.feeling.setImageResource(reactions[message.feeling])
                viewHolder.binding.feeling.visibility = View.VISIBLE
            } else {
                viewHolder.binding.feeling.visibility = View.GONE
            }
            viewHolder.binding.message.setOnTouchListener { v, event ->
                val isFeelingsEnabled = remoteConfig.getBoolean("isFeelingsEnabled")
                if (isFeelingsEnabled) popup.onTouch(v, event) else Toast.makeText(
                    context,
                    "This feature is disabled temporarily.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            viewHolder.binding.image.setOnTouchListener { v, event ->
                popup.onTouch(v, event)
                false
            }
            viewHolder.itemView.setOnLongClickListener {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.delete_dialog, null)
                val binding: DeleteDialogBinding = DeleteDialogBinding.bind(view)
                val dialog: AlertDialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()
                if (remoteConfig.getBoolean("isEveryoneDeletionEnabled")) {
                    binding.everyone.visibility = View.VISIBLE
                } else {
                    binding.everyone.visibility = View.GONE
                }
                binding.everyone.setOnClickListener(View.OnClickListener {
                    message.message = "This message is removed."
                    message.feeling = -1
                    FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.messageId!!).setValue(message)
                    FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .child(message.messageId!!).setValue(message)
                    dialog.dismiss()
                })
                binding.delete.setOnClickListener(View.OnClickListener {
                    FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.messageId!!).setValue(null)
                    dialog.dismiss()
                })
                binding.cancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
                dialog.show()
                false
            }
        } else {
            val viewHolder = holder as ReceiverViewHolder
            if (message.message == "photo") {
                viewHolder.binding.image.visibility = View.VISIBLE
                viewHolder.binding.message.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.binding.image)
            }
            viewHolder.binding.message.text = message.message
            if (message.feeling >= 0) {
                //message.setFeeling(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setImageResource(reactions[message.feeling])
                viewHolder.binding.feeling.visibility = View.VISIBLE
            } else {
                viewHolder.binding.feeling.visibility = View.GONE
            }
            viewHolder.binding.message.setOnTouchListener(OnTouchListener { v, event ->
                popup.onTouch(v, event)
                false
            })
            viewHolder.binding.image.setOnTouchListener(OnTouchListener { v, event ->
                popup.onTouch(v, event)
                false
            })
            viewHolder.itemView.setOnLongClickListener {
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.delete_dialog, null)
                val binding: DeleteDialogBinding = DeleteDialogBinding.bind(view)
                val dialog: AlertDialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()
                binding.everyone.setOnClickListener(View.OnClickListener {
                    message.message = "This message is removed."
                    message.feeling = -1
                    FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.messageId!!).setValue(message)
                    FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .child(message.messageId!!).setValue(message)
                    dialog.dismiss()
                })
                binding.delete.setOnClickListener(View.OnClickListener {
                    FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.messageId!!).setValue(null)
                    dialog.dismiss()
                })
                binding.cancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
                dialog.show()
                false
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemSentBinding

        init {
            binding = ItemSentBinding.bind(itemView)
        }
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemReceiveBinding

        init {
            binding = ItemReceiveBinding.bind(itemView)
        }
    }

    init {
        this.context = context
        this.messages = messages
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }



}