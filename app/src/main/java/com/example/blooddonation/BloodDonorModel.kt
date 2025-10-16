package com.example.blooddonation.model

data class BloodDonorModel(
    val name: String,
    val bloodGroup: String,
    val location: String,
    val timeAgo: String,
    val isUrgent: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val requestStatus: String = "active",
    val phone: String = ""
)
