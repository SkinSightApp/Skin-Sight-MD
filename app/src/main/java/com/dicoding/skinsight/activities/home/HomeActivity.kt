package com.dicoding.skinsight.activities.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.skinsight.activities.login.LoginActivity
import com.dicoding.skinsight.activities.login.dataStore
import com.dicoding.skinsight.activities.skindetection.SkinDetectionActivity
import com.dicoding.skinsight.databinding.ActivityHomeBinding
import com.dicoding.skinsight.models.MainViewModel
import com.dicoding.skinsight.models.login.LoginViewModel
import com.dicoding.skinsight.models.login.LoginViewModelFactory
import com.dicoding.skinsight.preferences.UserPreference

class HomeActivity : AppCompatActivity() {


    private lateinit var binding : ActivityHomeBinding
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private val preferences = UserPreference.getInstance(dataStore)
    private lateinit var token: String
    private lateinit var name : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleLogoutButton()
        getUserProfile()

        binding.btnSkinDetection.setOnClickListener {
            val intent = Intent(this, SkinDetectionActivity::class.java)
            startActivity(intent)
        }


    }

    private fun getUserProfile(){
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(preferences))[LoginViewModel::class.java]
        loginViewModel.getToken().observe(this) {
            token = it
            mainViewModel.getProfileData(it)
        }
        mainViewModel.profile.observe(this) { user ->
            name = user.name
            binding.tvName.text = "Hello, " + name
            binding.tvName.visibility = View.VISIBLE
        }

    }

    private fun handleLogoutButton(){
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(preferences))[LoginViewModel::class.java]

        binding.btnLogout.setOnClickListener {
            loginViewModel.logout()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}