package com.dicoding.skinsight.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.skinsight.networking.api.ApiConfig
import com.dicoding.skinsight.networking.response.CatalogProduct
import com.dicoding.skinsight.networking.response.CatalogResponse
import com.dicoding.skinsight.networking.response.LoginDataAccount
import com.dicoding.skinsight.networking.response.PredictionData
import com.dicoding.skinsight.networking.response.PredictionResponse
import com.dicoding.skinsight.networking.response.ProfileDataAccount
import com.dicoding.skinsight.networking.response.RegisterDataAccount
import com.dicoding.skinsight.networking.response.ResponseLogin
import com.dicoding.skinsight.networking.response.ResponseRegister
import com.dicoding.skinsight.networking.response.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File

class MainViewModel : ViewModel() {
    private val _isLoadingLogin = MutableLiveData<Boolean>()
    val isLoadingLogin: LiveData<Boolean> = _isLoadingLogin
    var isErrorLogin: Boolean = false
    private val _statusLogin = MutableLiveData<String>()
    val statusLogin: LiveData<String> = _statusLogin
    private val _userLogin = MutableLiveData<ResponseLogin>()
    val userLogin: LiveData<ResponseLogin> = _userLogin


    var isErrorRegist: Boolean = false
    private val _isLoadingRegister = MutableLiveData<Boolean>()
    val isLoadingRegister: LiveData<Boolean> = _isLoadingRegister
    private val _statusRegister = MutableLiveData<String>()
    val statusRegister: LiveData<String> = _statusRegister
    private val _messageRegister = MutableLiveData<String>()
    val messageRegister: LiveData<String> = _messageRegister


    private val _profile = MutableLiveData<User>()
    val profile: LiveData<User> = _profile


    private val _isLoadingPredict = MutableLiveData<Boolean>()
    val isLoadingPredict: LiveData<Boolean> = _isLoadingPredict
    private val _isErrorPredict = MutableLiveData<Boolean>()
    val isErrorPredict: LiveData<Boolean> = _isErrorPredict
    private val _predictionResult = MutableLiveData<PredictionData>()
    val predictionResult: LiveData<PredictionData> = _predictionResult

    fun getResponseLogin(loginDataAccount: LoginDataAccount) {
        _isLoadingLogin.value = true
        val api = ApiConfig.getApiService().loginUser(loginDataAccount)
        api.enqueue(object : retrofit2.Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                _isLoadingLogin.value = false
                val responseBody = response.body()
                if (response.isSuccessful) {
                    isErrorLogin = false
                    _userLogin.value = responseBody!!
                    _statusLogin.value = _userLogin.value!!.status
                } else {
                    isErrorLogin = true
                    _statusLogin.value = "fail"
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                isErrorLogin = true
                _isLoadingLogin.value = false
            }
        })
    }

    fun getResponseRegister(registDataUser: RegisterDataAccount) {
        _isLoadingRegister.value = true
        val api = ApiConfig.getApiService().registerUser(registDataUser)
        api.enqueue(object : retrofit2.Callback<ResponseRegister> {
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>
            ) {
                _isLoadingRegister.value = false
                if (response.isSuccessful) {
                    isErrorRegist = false
                    _statusRegister.value = response.body()?.status.toString()
                    _messageRegister.value = response.body()?.message.toString()
                    Log.d("MainViewModel", "Registration successful: ${response.body()?.message}")
                } else {
                    isErrorRegist = true
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody != null && errorBody.isNotEmpty()) {
                        try {
                            JSONObject(errorBody).getString("message")
                        } catch (e: JSONException) {
                            "Registration failed"
                        }
                    } else {
                        "Registration failed"
                    }
                    _statusRegister.value = errorMessage
                    _messageRegister.value = errorMessage
                    Log.d("MainViewModel", "Registration failed: $errorMessage")
                }
            }


            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                isErrorRegist = true
                _isLoadingRegister.value = false
                _statusRegister.value = "Error message: " + t.message.toString()
                Log.d("MainViewModel", "Registration error: ${t.message}")
            }
        })
    }

    fun getProfileData(token: String) {
//        _isLoading.value = true
        val api = ApiConfig.getApiService().getProfile("Bearer $token")
        api.enqueue(object : retrofit2.Callback<ProfileDataAccount> {
            override fun onResponse(
                call: Call<ProfileDataAccount>,
                response: Response<ProfileDataAccount>
            ) {
//                _isLoading.value = false
                if (response.isSuccessful) {
//                    isError = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _profile.value = responseBody.data.user
                    }
//                    _message.value = responseBody?.message.toString()
                } else {
//                    isError = true
//                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<ProfileDataAccount>, t: Throwable) {
//                _isLoading.value = false
//                isError = true
//                _message.value = t.message.toString()
            }
        })
    }


    fun getPrediction(file: File) {
        val api = ApiConfig.getApiServicePredict().predict(
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
        )
        api.enqueue(object : retrofit2.Callback<PredictionResponse> {
            override fun onResponse(
                call: Call<PredictionResponse>,
                response: Response<PredictionResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _predictionResult.value = responseBody.data
                    } else {
                        Log.d("Prediction Status", "Failed")
                    }
                }
            }

            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }


    private val _acneCatalog = MutableLiveData<List<CatalogProduct>>()
    val acneCatalog: LiveData<List<CatalogProduct>> = _acneCatalog

    private val _blackheadCatalog = MutableLiveData<List<CatalogProduct>>()
    val blackheadCatalog: LiveData<List<CatalogProduct>> = _blackheadCatalog

    private val _rednessCatalog = MutableLiveData<List<CatalogProduct>>()
    val rednessCatalog: LiveData<List<CatalogProduct>> = _rednessCatalog

    fun getProducts(token: String) {
        _isLoadingPredict.value = true
        val api = ApiConfig.getApiService().getCatalogs("Bearer $token")
        api.enqueue(object : retrofit2.Callback<CatalogResponse> {
            override fun onResponse(
                call: Call<CatalogResponse>,
                response: Response<CatalogResponse>
            ) {
                _isLoadingPredict.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val catalogData = response.body()?.data
                        catalogData?.redness?.let { rednessCategory ->
                            _rednessCatalog.value = rednessCategory.products
                        }
                        catalogData?.acne?.let { acneCategory ->
                            _acneCatalog.value = acneCategory.products
                        }
                        catalogData?.blackhead?.let { blackheadCategory ->
                            _blackheadCatalog.value = blackheadCategory.products
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CatalogResponse>, t: Throwable) {
                _isLoadingPredict.value = false
//                _isLoading.value = false
//                isError = true
//                _message.value = t.message.toString()
            }
        })
    }

}
