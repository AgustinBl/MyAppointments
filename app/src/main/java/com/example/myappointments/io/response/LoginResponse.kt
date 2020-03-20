package com.example.myappointments.io.response

import com.example.myappointments.model.User

data class LoginResponse(val success: Boolean, val user: User, val jwt: String)