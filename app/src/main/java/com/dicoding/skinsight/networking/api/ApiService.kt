package com.dicoding.skinsight.networking.api

import com.dicoding.skinsight.networking.response.LoginDataAccount
import com.dicoding.skinsight.networking.response.ProfileDataAccount
import com.dicoding.skinsight.networking.response.RegisterDataAccount
import com.dicoding.skinsight.networking.response.ResponseLogin
import com.dicoding.skinsight.networking.response.ResponseRegister
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun loginUser(
        @Body requestLogin: LoginDataAccount,
    ): Call<ResponseLogin>

    @POST("register")
    fun registerUser(
        @Body requestRegister: RegisterDataAccount,
    ): Call<ResponseRegister>

    @GET("user")
    fun getProfile(
        @Header("Authorization") token: String,
    ): Call<ProfileDataAccount>

}