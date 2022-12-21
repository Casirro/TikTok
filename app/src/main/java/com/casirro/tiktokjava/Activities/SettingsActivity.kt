package com.casirro.tiktokjava.Activities

import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.casirro.tiktokjava.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    var binding: ActivitySettingsBinding? = null
    var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar!!.hide()
        auth = FirebaseAuth.getInstance()
        binding!!.odhlasitView.setOnClickListener {
            auth!!.signOut()
            startActivity(Intent(this@SettingsActivity, SignInActivity::class.java))
            finish()
        }
        binding!!.imageView18.setOnClickListener { finish() }
    }
}