package com.example.myappointments.model

import com.google.gson.annotations.SerializedName

data class Appointment (
    val id: Int,
    val description: String,
    @SerializedName("schedule_date")
    val scheduledDate: String,
    val type: String,
    @SerializedName("created_at")
    val createdAt: String,
    val status: String,
    @SerializedName("schedule_time_12")
    val scheduledTime: String,
    val specialty: Specialty,
    val doctor: Doctor
)