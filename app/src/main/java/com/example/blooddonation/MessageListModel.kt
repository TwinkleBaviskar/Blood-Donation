package com.example.blooddonation.model

data class MessageListModel(
    var userId: String = "",
    var name: String = "",
    var lastMessage: String = "",
    var lastTimestamp: Long = 0L,
    var unreadCount: Int = 0
)
