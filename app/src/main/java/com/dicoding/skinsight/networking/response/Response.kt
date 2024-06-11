package com.dicoding.skinsight.networking.response

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
    var userId : String

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
