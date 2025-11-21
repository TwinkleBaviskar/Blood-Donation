package com.example.blooddonation.model

data class ReportModel(
    var name: String? = null,
    var bloodGroup: String? = null,
    var weight: String? = null,
    var lastDonation: String? = null,
    var nextDonation: String? = null,
    var hemoglobin: String? = null,
    var eligibility: String? = null
)
