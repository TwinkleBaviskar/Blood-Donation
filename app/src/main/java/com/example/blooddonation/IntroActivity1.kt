package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonation.databinding.ActivityIntro1Binding

class IntroActivity1 : AppCompatActivity() {
    private lateinit var intro1Binding: ActivityIntro1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intro1Binding=ActivityIntro1Binding.inflate(layoutInflater)
        setContentView(intro1Binding.root)

        intro1Binding.btnSkip.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish() // Optional: prevent going back
        }

        // Handle arrow button click -> Go to next intro screen
        intro1Binding.btnNext.setOnClickListener {
            val intent = Intent(this, IntroActivity2::class.java)
            startActivity(intent)
            finish() // Optional
        }



    }
}