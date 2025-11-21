package com.example.blooddonation.model

data class BloodDonorModel(
    val userId: String = "",             // requester UID
    val name: String = "",               // patient name (UI list)
    val requesterName: String = "",      // 🔥 correct chat name
    val bloodGroup: String = "",
    val location: String = "",
    val timeAgo: String = "",
    val isUrgent: Boolean = false,
    val timestamp: Long = 0L,
    val requestStatus: String = "active",
    val phone: String = "",
    val requestId: String = ""
)
