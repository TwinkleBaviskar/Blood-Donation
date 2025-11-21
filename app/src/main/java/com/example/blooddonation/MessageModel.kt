package com.example.blooddonation.model

data class MessageModel(
    var senderId: String = "",
    var receiverId: String = "",
    var text: String = "",
    var timestamp: Long = System.currentTimeMillis()
)
