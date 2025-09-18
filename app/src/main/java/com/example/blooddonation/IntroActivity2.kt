package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonation.databinding.ActivityIntro2Binding

class IntroActivity2 : AppCompatActivity() {

    private lateinit var intro2Binding: ActivityIntro2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intro2Binding=ActivityIntro2Binding.inflate(layoutInflater)
        setContentView(intro2Binding.root)

        intro2Binding.btnSkip.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish() // Optional: prevent going back
        }

        // Handle arrow button click -> Go to next intro screen
        intro2Binding.btnNext.setOnClickListener {
            val intent = Intent(this, IntroActivity3::class.java)
            startActivity(intent)
            finish() // Optional
        }


    }
}