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


data class CatalogResponse(
    val status: String,
    val message: String,
    val data: CatalogData
)

data class CatalogData(
    val redness: CatalogCategory,
    val acne: CatalogCategory,
    val blackhead: CatalogCategory
)

data class CatalogCategory(
    val id: String,
    val createdAt: String,
    val category: String,
    val updatedAt: String,
    val products: List<CatalogProduct>
)

data class CatalogProduct(
    val id: String,
    val createdAt: String,
    val reviews: Int,
    val price: String,
    val imageUrl: String,
    val name: String,
    val rating: Float,  // Diubah dari Int ke Float
    val brand: String,
    val url: String,
    val updatedAt: String
)
