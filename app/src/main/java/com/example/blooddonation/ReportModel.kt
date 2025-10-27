package com.example.blooddonation.model

data class ReportModel(
    val name: String,
    val bloodGroup: String,
    val weight: String,
    val lastDonation: String,
    val nextDonation: String,
    val hemoglobin: String,
    val eligibility: String
)
