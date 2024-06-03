package com.dicoding.skinsight.networking.api

import com.dicoding.skinsight.networking.response.LoginDataAccount
import com.dicoding.skinsight.networking.response.ResponseLogin
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun loginUser(@Body requestLogin: LoginDataAccount): Call<ResponseLogin>
}