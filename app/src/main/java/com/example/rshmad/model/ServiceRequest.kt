package com.example.rshmad.model



data class ServiceRequest(
    val serviceType: String,
    val customerName: String,
    val customerPhone: String,
    val status: String = "Pending"
)
