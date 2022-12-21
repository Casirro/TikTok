package com.casirro.tiktokjava.Activities

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.app.ProgressDialog
import android.graphics.Color
import android.widget.Toast
import com.casirro.tiktokjava.Modules.User
import com.casirro.tiktokjava.databinding.ActivityPrihlasitSeBinding

class PrihlasitSeActivity : AppCompatActivity() {
    var binding: ActivityPrihlasitSeBinding? = null
    var progressDialog: ProgressDialog? = null
    var auth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrihlasitSeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar!!.hide()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        progressDialog = ProgressDialog(this@PrihlasitSeActivity)
        progressDialog!!.setTitle("Login")
        progressDialog!!.setMessage("Prosím počkejte, \n Validation in Progress")
        binding!!.view10.setOnClickListener {
            binding!!.view10.setBackgroundColor(Color.rgb(234, 67, 89))
            binding!!.textView26.setTextColor(Color.WHITE)
            if (binding!!.editTextTextEmailAddress.text.toString().isNotEmpty() && binding!!.editTextTextPassword.text.toString()
                    .isNotEmpty()
            ) {
                progressDialog!!.show()
                auth!!.signInWithEmailAndPassword(
                    binding!!.editTextTextEmailAddress.text.toString(),
                    binding!!.editTextTextPassword.text.toString()
                ).addOnCompleteListener { task ->
                    progressDialog!!.dismiss()
                    if (task.isSuccessful) {
                        val intent =
                            Intent(this@PrihlasitSeActivity, ProfileActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@PrihlasitSeActivity,
                            task.getException()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    this@PrihlasitSeActivity,
                    "Enter Credentials",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding!!.imageView17.setOnClickListener { finish() }
    }
}