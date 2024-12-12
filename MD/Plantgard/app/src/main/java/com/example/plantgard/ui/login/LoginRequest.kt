package com.example.plantgard.ui.login

import java.io.Serializable

data class LoginRequest(
    val email: String,
    val password: String
) : Serializable
