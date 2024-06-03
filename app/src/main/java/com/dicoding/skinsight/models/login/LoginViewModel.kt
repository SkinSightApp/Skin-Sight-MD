package com.dicoding.skinsight.models.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.skinsight.preferences.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val userPreference: UserPreference): ViewModel() {

    fun getLoginSession(): LiveData<Boolean> {
        return userPreference.getLoginSession().asLiveData()
    }
    fun saveLoginSession(loginSession: Boolean) {
        viewModelScope.launch {
            userPreference.saveLoginSession(loginSession)
        }
    }
    fun getToken(): LiveData<String> {
        return userPreference.getToken().asLiveData()
    }
    fun saveToken(token: String) {
        viewModelScope.launch {
            userPreference.saveToken(token)
        }
    }
    fun logout() {
        viewModelScope.launch {
            userPreference.logout()
        }
    }

}