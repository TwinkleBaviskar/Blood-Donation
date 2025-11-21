package com.example.blooddonation

import DonorModel
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

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

        // Get donor data
        val donor = intent.getSerializableExtra("donorData") as DonorModel

        // 🩸 Set all donor data
        profileName.text = donor.fullName ?: "Unknown Donor"
        lastDonation.text = "Last Donation: ${donor.lastDonation ?: "-"}"
        bloodTypeValue.text = donor.bloodGroup ?: "-"
        donatedValue.text = donor.totalDonations?.toString() ?: "0"
        lifeSavedValue.text = donor.livesSaved?.toString() ?: "0"

        // 👤 Load image
        if (!donor.profileImage.isNullOrEmpty()) {
            Glide.with(this)
                .load(donor.profileImage)
                .placeholder(R.drawable.profile)
                .into(profileImage)
        } else {
            profileImage.setImageResource(R.drawable.profile)
        }

        // 🔙 Back button
        backButton.setOnClickListener { finish() }

        // 📞 Call donor
        callButton.setOnClickListener {
            donor.mobile?.let {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$it"))
                startActivity(dialIntent)
            } ?: Toast.makeText(this, "No phone number available", Toast.LENGTH_SHORT).show()
        }

        // 🩸 Request donor → yahan se RequestActivity open hogi
        requestButton.setOnClickListener {

            // (1) Optional: jo tum pehle se kar rahi thi, wo bhi rakh sakti ho
            val sharedPref = getSharedPreferences("RequestedDonors", MODE_PRIVATE)
            val editor = sharedPref.edit()

            val donorData = "${donor.fullName}|${donor.bloodGroup}|${donor.mobile}"
            val existing = sharedPref.getStringSet("donors", mutableSetOf()) ?: mutableSetOf()
            existing.add(donorData)

            editor.putStringSet("donors", existing)
            editor.apply()

            // (2) Ab RequestActivity open karo + data pass karo
            val intent = Intent(this, RequestActivity::class.java)
            intent.putExtra("donorId", donor.userId)
            intent.putExtra("donorName", donor.fullName)
            intent.putExtra("donorBloodGroup", donor.bloodGroup)
            intent.putExtra("donorMobile", donor.mobile)
            intent.putExtra("donorLastDonation", donor.lastDonation)
            startActivity(intent)
        }

        // 🚫 Hide buttons if viewing own profile
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (donor.userId == currentUserId) {
            callButton.visibility = View.GONE
            requestButton.visibility = View.GONE
        }
    }
}
