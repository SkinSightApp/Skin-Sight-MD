package com.dicoding.skinsight.networking.response

data class LoginDataAccount(
    var email: String,
    var password: String
)
data class LoginResult(
    var userId: String,
    var name: String,
    var token: String
)
data class ResponseLogin(
    var error: Boolean,
    var message: String,
    var loginResult: LoginResult
)

