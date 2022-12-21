package com.casirro.tiktokjava.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import com.casirro.tiktokjava.databinding.ActivityVideoProceedBinding

class videoProceedActivity : AppCompatActivity() {
    var binding: ActivityVideoProceedBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoProceedBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar!!.hide()
        val uri: String? = intent.getStringExtra("VideoUri")
        val video: Uri = Uri.parse(uri)
        binding!!.frameLay.setVideoURI(video)
        binding!!.frameLay.start()
        binding!!.frameLay.setOnPreparedListener { mp -> mp.isLooping = true }
        binding!!.view18.bringToFront()
        binding!!.imageView32.bringToFront()
        binding!!.imageView33.bringToFront()
        binding!!.imageView34.bringToFront()
        binding!!.view17.setOnClickListener {
            val intent =
                Intent(this@videoProceedActivity, ShareVideoActivity::class.java)
            intent.putExtra("VideoUri", uri)
            startActivity(intent)
        }
        binding!!.imageView32.setOnClickListener { finish() }
    }
}