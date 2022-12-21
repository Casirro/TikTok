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
import com.casirro.tiktokjava.databinding.ActivityRegister2Binding

class Register2Activity : AppCompatActivity() {
    var binding: ActivityRegister2Binding? = null
    var auth: FirebaseAuth? = null
    var progressDialog: ProgressDialog? = null
    var database: FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister2Binding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar!!.hide()
        progressDialog = ProgressDialog(this@Register2Activity)
        progressDialog!!.setTitle("Vytváření účtu")
        progressDialog!!.setMessage("Vytváříme váš účet")
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        binding!!.imageView18.setOnClickListener { finish() }
        binding!!.view10.setOnClickListener {
            binding!!.view10.setBackgroundColor(Color.rgb(234, 67, 89))
            binding!!.textView25.setTextColor(Color.WHITE)
            if (binding!!.editTextTextEmailAddress2.text.toString().isNotEmpty() && binding!!.editTextTextPassword2.text.toString()
                    .isNotEmpty()
            ) {
                progressDialog!!.show()
                auth!!.createUserWithEmailAndPassword(
                    binding!!.editTextTextEmailAddress2.text.toString(),
                    binding!!.editTextTextPassword2.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val username =
                            "@" + binding!!.editTextTextEmailAddress2.text.toString()
                                .replace("@gmail.com", "")
                        val mail = binding!!.editTextTextEmailAddress2.text.toString()
                        val password = binding!!.editTextTextPassword2.text.toString()
                        val uid = auth!!.uid
                        val user = User(uid, mail, username, password)
                        database!!.reference.child("users").child((uid)!!).setValue(user)
                        Toast.makeText(
                            this@Register2Activity,
                            "Úspěšně registrováno",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(
                            Intent(
                                this@Register2Activity,
                                ProfileActivity::class.java
                            )
                        )
                        finish()
                    }
                }
            }
        }
    }
}