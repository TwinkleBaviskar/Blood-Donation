package com.example.blooddonation.model

data class NotificationModel(
    var notificationId: String = "",
    var title: String = "",
    var message: String = "",
    var timestamp: Long = 0L
)
