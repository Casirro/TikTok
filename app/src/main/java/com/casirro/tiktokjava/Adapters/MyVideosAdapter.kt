package com.casirro.tiktokjava.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.casirro.tiktokjava.Activities.VideoActivity
import com.casirro.tiktokjava.Modules.Video
import com.casirro.tiktokjava.R
import com.casirro.tiktokjava.databinding.MyVideosBinding

class MyVideosAdapter (var videos: List<Video>, var context: Context) :
    RecyclerView.Adapter<MyVideosAdapter.MyVideosViewHolder>(){
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyVideosViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.my_videos, viewGroup, false)
        return MyVideosViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyVideosViewHolder, position: Int) {
        val videoO = videos[position]
        val uri: Uri = Uri.parse(videoO.videoPath.toString())
        Glide.with(context).load(uri).into(holder.binding.videoView2)


        holder.binding.videoView2.setOnClickListener {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("videoUid", videoO.videoId)
            intent.putExtra("videoPath", videoO.videoPath)
            intent.putExtra("caption", videoO.caption)
            intent.putExtra("uid", videoO.userId)

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

        }


    }



    override fun getItemCount(): Int {
        return videos.size
    }
    inner class MyVideosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var binding: MyVideosBinding

        init {
            binding = MyVideosBinding.bind(itemView)
        }
    }
}


