package com.casirro.tiktokjava.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.casirro.tiktokjava.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    var binding: ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar!!.hide()
        binding!!.textView17.setOnClickListener {
            startActivity(
                Intent(
                    this@SignUpActivity,
                    SignInActivity::class.java
                )
            )
        }
        binding!!.view4.setOnClickListener {
            startActivity(
                Intent(
                    this@SignUpActivity,
                    Register2Activity::class.java
                )
            )
        }
    }
}