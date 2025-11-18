package com.example.blooddonation

import java.io.Serializable

// This model matches EXACTLY the fields saved in Firebase during registration.
data class DonorModel(
    val userId: String? = null,
    val fullName: String? = null,      // 🔹 matches Firebase key "fullName"
    val email: String? = null,
    val mobile: String? = null,
    val age: String? = null,
    val gender: String? = null,
    val bloodGroup: String? = null,
    val weight: String? = null,
    val hemoglobin: String? = null,
    val lastDonation: String? = null,
    val livesSaved: Int? = 0,          // optional stats field
    val totalDonations: Int? = 0,      // optional stats field
    val profileImage: String? = null   // optional profile image (for future)
) : Serializable
