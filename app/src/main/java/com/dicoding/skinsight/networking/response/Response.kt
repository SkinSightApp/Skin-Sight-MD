package com.dicoding.skinsight.networking.response

import java.io.File

data class LoginDataAccount(
    var email: String,
    var password: String
)

data class ResponseLogin(
    var status: String,
    var message: String,
    var data: LoginResult
)

data class LoginResult(
    var token: String,
)

data class RegisterDataAccount(
    var name: String,
    var email: String,
    var password: String
)

data class ResponseRegister(
    var status: String,
    var message: String,
    var userId: String

)

data class ProfileDataAccount(
    var status: String,
    var message: String,
    var data: ProfileData
)

data class ProfileData(
    var user: User
)

data class User(
    var createdAt: String,
    var password: String,
    var name: String,
    var email: String,
    var updatedAt: String
)

data class PredictionResponse(
    val status: String,
    val message: String,
    val data: PredictionData
)

data class PredictionData(
    val classes: List<String>,
    val prob: List<Double>,
    val top_2: Map<String, Double>
)

data class Product(
    val brand: String,
    val name: String,
    val price: String,
    val category: String,
    val rating: Double,
    val reviews: Int,
    val url: String,
    val image_url: String
)
