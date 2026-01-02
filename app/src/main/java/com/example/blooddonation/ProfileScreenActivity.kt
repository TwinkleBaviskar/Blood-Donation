package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.blooddonation.databinding.ActivityProfileScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("YOUR DATABASE API")

        val userId = auth.currentUser?.uid
        if (userId != null) {
            userRef = database.getReference("users").child(userId)
            loadUserProfile()
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            Toast.makeText(this, "Settings feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.btnAbout.setOnClickListener {
            Toast.makeText(this, "Blood Donation App v1.0\nDeveloped by Twinkle and Disha ❤️", Toast.LENGTH_LONG).show()
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadUserProfile() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("fullName").getValue(String::class.java) ?: "User"
                    val lastDonation = snapshot.child("lastDonation").getValue(String::class.java) ?: "-"
                    val imageUrl = snapshot.child("profileImage").getValue(String::class.java)

                    binding.txtUserName.text = name
                    binding.txtLastDonation.text = "Last Donation: $lastDonation"

                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this@ProfileScreenActivity)
                            .load(imageUrl)
                            .placeholder(R.drawable.profile)
                            .circleCrop()
                            .into(binding.profileImage)
                    }
                } else {
                    Toast.makeText(this@ProfileScreenActivity, "No profile data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ProfileScreenActivity,
                    "Failed to load profile: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
