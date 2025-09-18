package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonation.databinding.ActivityDonorProfileBinding

class DonorProfileActivity : AppCompatActivity() {

    private lateinit var profileBinding: ActivityDonorProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        profileBinding = ActivityDonorProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        // 🔙 Back button click: Navigate to HomeActivity (or your main screen)
        profileBinding.backButton.setOnClickListener {
            val intent = Intent(this, HomeScreenActivity::class.java) // Change this if needed
            startActivity(intent)
           }
    }
}
