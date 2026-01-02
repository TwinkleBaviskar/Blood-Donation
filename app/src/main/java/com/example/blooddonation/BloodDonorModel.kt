package com.example.blooddonation.model

data class BloodDonorModel(
    val userId: String = "",
    val name: String = "",
    val requesterName: String = "",
    val bloodGroup: String = "",
    val location: String = "",
    val timeAgo: String = "",
    val isUrgent: Boolean = false,
    val timestamp: Long = 0L,
    val requestStatus: String = "active",
    val phone: String = "",
    val requestId: String = ""
)
