package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonation.databinding.ActivityAuthActivityBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var authActivityBinding: ActivityAuthActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authActivityBinding=ActivityAuthActivityBinding.inflate(layoutInflater)
        setContentView(authActivityBinding.root)

        authActivityBinding.btnclick1.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: prevent going back
        }

        // Handle arrow button click -> Go to next intro screen
        authActivityBinding.btnclick2.setOnClickListener {
            val intent = Intent(this,RegistrationActivity::class.java)
            startActivity(intent)
            finish() // Optional
        }


    }
}