package com.example.blooddonation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DonorProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_profile)

        // Views
        val backButton: ImageView = findViewById(R.id.backButton)
        val profileImage: ImageView = findViewById(R.id.profileImage)
        val profileName: TextView = findViewById(R.id.profileName)
        val lastDonation: TextView = findViewById(R.id.lastDonation)
        val callButton: TextView = findViewById(R.id.callButton)
        val requestButton: TextView = findViewById(R.id.requestButton)

        val donatedValue: TextView = findViewById(R.id.donatedValue)
        val bloodTypeValue: TextView = findViewById(R.id.bloodTypeValue)
        val lifeSavedValue: TextView = findViewById(R.id.lifeSavedValue)

        // Get DonorModel from intent
        val donor = intent.getSerializableExtra("donorData") as DonorModel

        // Set data to views
        profileImage.setImageResource(donor.imageRes)
        profileName.text = donor.name
        lastDonation.text = "Last Donation: ${donor.lastDonation}"
        donatedValue.text = donor.totalDonations.toString()
        bloodTypeValue.text = donor.bloodGroup
        lifeSavedValue.text = donor.livesSaved.toString()

        // 🔙 Back button
        backButton.setOnClickListener {
            finish()
        }

        // 📞 Call button (dummy phone number)
        callButton.setOnClickListener {
            val phone = "1234567890" // ideally donor phone
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            startActivity(dialIntent)
        }

        // 🩸 Request button — store donor locally
        requestButton.setOnClickListener {
            Toast.makeText(this, "Request sent to ${donor.name}", Toast.LENGTH_SHORT).show()

            // Save requested donor locally (for DonateFragment)
            val sharedPref = getSharedPreferences("RequestedDonors", MODE_PRIVATE)
            val editor = sharedPref.edit()

            // Convert donor info into a single string (name|bloodGroup|location)
            val donorData = "${donor.name}|${donor.bloodGroup}|${donor.location}"
            val existing = sharedPref.getStringSet("donors", mutableSetOf()) ?: mutableSetOf()
            existing.add(donorData)

            editor.putStringSet("donors", existing)
            editor.apply()
        }
    }
}
