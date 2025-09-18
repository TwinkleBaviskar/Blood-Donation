package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonation.databinding.ActivityIntro3Binding

class IntroActivity3 : AppCompatActivity() {
    private lateinit var intro3Binding: ActivityIntro3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intro3Binding=ActivityIntro3Binding.inflate(layoutInflater)
        setContentView(intro3Binding.root)

        intro3Binding.btnSkip.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish() // Optional: prevent going back
        }

        // Handle arrow button click -> Go to next intro screen
        intro3Binding.btnNext.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish() // Optional
        }


    }
}