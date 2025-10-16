package com.example.blooddonation.model

data class MessageModel(
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
