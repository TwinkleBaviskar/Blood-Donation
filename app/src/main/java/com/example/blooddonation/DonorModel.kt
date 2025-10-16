package com.example.blooddonation

import java.io.Serializable

data class DonorModel(
    val name: String,
    val location: String,
    val bloodGroup: String,
    val imageRes: Int,
    val lastDonation: String,
    val livesSaved: Int,
    val totalDonations: Int
) : Serializable   // 👈 add Serializable
