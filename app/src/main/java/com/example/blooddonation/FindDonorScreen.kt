package com.example.blooddonation

import DonorModel
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import androidx.core.widget.doOnTextChanged
import java.util.Random

class FindDonorScreen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonorAdapter
    private lateinit var edtSearch: EditText

    private val donorList = ArrayList<DonorModel>()
    private val allDonors = ArrayList<DonorModel>()

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_donor_screen)

        val backArrow: ImageView = findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            startActivity(Intent(this, HomeScreenActivity::class.java))
            finish()
        }

        recyclerView = findViewById(R.id.recyclerDonors)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        edtSearch = findViewById(R.id.edtSearchDonor)

        database = FirebaseDatabase.getInstance("YOUR DATABASE API")
        userRef = database.getReference("users")

        adapter = DonorAdapter(this, donorList)
        recyclerView.adapter = adapter

        setupSearch()
        loadDonorsFromFirebase()
    }

    private fun setupSearch() {
        edtSearch.doOnTextChanged { text, _, _, _ ->
            val q = text?.toString()?.trim()?.lowercase() ?: ""
            if (q.isEmpty()) {
                adapter.updateList(allDonors)
            } else {
                val filtered = allDonors.filter { donor ->
                    val name = donor.fullName?.lowercase() ?: ""
                    val bg = donor.bloodGroup?.lowercase() ?: ""
                    name.contains(q) || bg.contains(q)
                }
                adapter.updateList(filtered)
            }
        }
    }

    private fun loadDonorsFromFirebase() {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                donorList.clear()
                allDonors.clear()

                if (snapshot.exists()) {
                    val random = Random()

                    for (userSnap in snapshot.children) {
                        try {
                            val userId = userSnap.child("userId").value?.toString()
                                ?: userSnap.key

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

                            val latVal = userSnap.child("latitude").value
                            val latitude = when (latVal) {
                                is Double -> latVal
                                is Long -> latVal.toDouble()
                                is String -> latVal.toDoubleOrNull()
                                else -> null
                            }


                            val lonVal = userSnap.child("longitude").value
                            val longitude = when (lonVal) {
                                is Double -> lonVal
                                is Long -> lonVal.toDouble()
                                is String -> lonVal.toDoubleOrNull()
                                else -> null
                            }


                            val lsRaw = userSnap.child("livesSaved").value
                            var livesSaved = when (lsRaw) {
                                is Long -> lsRaw.toInt()
                                is Int -> lsRaw
                                is String -> lsRaw.toIntOrNull() ?: 0
                                else -> 0
                            }
                            if (livesSaved == 0) {
                                livesSaved = random.nextInt(5) + 1   // 1–5
                            }


                            val tdRaw = userSnap.child("totalDonations").value
                            var totalDonations = when (tdRaw) {
                                is Long -> tdRaw.toInt()
                                is Int -> tdRaw
                                is String -> tdRaw.toIntOrNull() ?: 0
                                else -> 0
                            }
                            if (totalDonations == 0) {
                                totalDonations = random.nextInt(3) + 1   // 1–3
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
                                profileImage = profileImage,
                                latitude = latitude,
                                longitude = longitude
                            )

                            donorList.add(donor)
                        } catch (e: Exception) {
                            Log.e("FindDonorScreen", "Error parsing donor: ${e.message}", e)
                        }
                    }

                    allDonors.addAll(donorList)
                    adapter.updateList(donorList)
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
