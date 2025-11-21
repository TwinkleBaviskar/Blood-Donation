package com.example.blooddonation

import DonorModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.database.*

class FindDonorScreen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonorAdapter
    private val donorList = ArrayList<DonorModel>()

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_donor_screen)

        // 🔙 Back button
        val backArrow: ImageView = findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            startActivity(Intent(this, HomeScreenActivity::class.java))
            finish()
        }

        recyclerView = findViewById(R.id.recyclerDonors)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        database = FirebaseDatabase.getInstance("https://blooddonation-bbec8-default-rtdb.asia-southeast1.firebasedatabase.app/")
        userRef = database.getReference("users")

        adapter = DonorAdapter(this, donorList)
        recyclerView.adapter = adapter

        loadDonorsFromFirebase()
    }

    private fun loadDonorsFromFirebase() {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                donorList.clear()

                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        try {
                            val userId = userSnap.child("userId").value?.toString()
                                ?: userSnap.key  // fallback: key as userId

                            val fullName = userSnap.child("fullName").value?.toString()
                            val email = userSnap.child("email").value?.toString()
                            val mobile = userSnap.child("mobile").value?.toString()
                            val age = userSnap.child("age").value?.toString()
                            val gender = userSnap.child("gender").value?.toString()
                            val bloodGroup = userSnap.child("bloodGroup").value?.toString()
                            val weight = userSnap.child("weight").value?.toString()
                            val hemoglobin = userSnap.child("hemoglobin").value?.toString()
                            val lastDonation = userSnap.child("lastDonation").value?.toString()
                            val profileImage = userSnap.child("profileImage").value?.toString()

                            // livesSaved: safely Int me convert karo
                            val livesSaved = when (val v = userSnap.child("livesSaved").value) {
                                is Long -> v.toInt()
                                is Int -> v
                                is String -> v.toIntOrNull() ?: 0
                                else -> 0
                            }

                            // totalDonations: safely Int me convert karo
                            val totalDonations = when (val v = userSnap.child("totalDonations").value) {
                                is Long -> v.toInt()
                                is Int -> v
                                is String -> v.toIntOrNull() ?: 0
                                else -> 0
                            }

                            val donor = DonorModel(
                                userId = userId,
                                fullName = fullName,
                                email = email,
                                mobile = mobile,
                                age = age,
                                gender = gender,
                                bloodGroup = bloodGroup,
                                weight = weight,
                                hemoglobin = hemoglobin,
                                lastDonation = lastDonation,
                                livesSaved = livesSaved,
                                totalDonations = totalDonations,
                                profileImage = profileImage
                            )

                            donorList.add(donor)
                        } catch (e: Exception) {
                            Log.e("FindDonorScreen", "Error parsing donor: ${e.message}", e)
                        }
                    }

                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@FindDonorScreen, "No donors found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FindDonorScreen, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
