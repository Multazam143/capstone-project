package com.example.plantgard.response

import java.io.Serializable

// robo pojo
data class PredictResponse(
	val data: Data,
	val message: String,
	val errors: Any
) : Serializable

data class User(
	val uid: String,
	val name: String,
	val email: String
) : Serializable

data class Data(
	val disease: Disease,
	val updatedAt: String,
	val createdAt: String,
	val plantType: String,
	val user: User
) : Serializable

data class Disease(
	val treatment: String,
	val description: String,
	val type: String,
	val prevention: String
) : Serializable