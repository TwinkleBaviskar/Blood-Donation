package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        // Find Donor button click
        val findDonorBtn = findViewById<ImageView>(R.id.imgFindDonor)
        findDonorBtn.setOnClickListener {
            val intent = Intent(this, FindDonorScreen::class.java)
            startActivity(intent)
        }
    }
}
