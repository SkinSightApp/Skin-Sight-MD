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

