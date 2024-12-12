package com.example.plantgard.response

import java.io.Serializable

data class LoginResponse(
    val message: String,
    val errors: Any?,
    val data: TokenData?
) : Serializable

data class TokenData(
    val token: String,
    val refresh_token: String
) : Serializable
