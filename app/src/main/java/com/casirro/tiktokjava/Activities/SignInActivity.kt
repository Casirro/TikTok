package com.casirro.tiktokjava.Activities

import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.casirro.tiktokjava.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    var binding: ActivitySignInBinding? = null
    var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar!!.hide()
        auth = FirebaseAuth.getInstance()
        binding!!.textView17.setOnClickListener {
            startActivity(
                Intent(
                    this@SignInActivity,
                    SignUpActivity::class.java
                )
            )
        }
        binding!!.view4.setOnClickListener {
            startActivity(
                Intent(
                    this@SignInActivity,
                    PrihlasitSeActivity::class.java
                )
            )
        }
        if (auth!!.currentUser != null) {
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}