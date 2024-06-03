package com.dicoding.skinsight.activities.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.skinsight.activities.login.LoginActivity
import com.dicoding.skinsight.activities.login.dataStore
import com.dicoding.skinsight.databinding.ActivityHomeBinding
import com.dicoding.skinsight.models.login.LoginViewModel
import com.dicoding.skinsight.models.login.LoginViewModelFactory
import com.dicoding.skinsight.preferences.UserPreference

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = UserPreference.getInstance(dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(preferences))[LoginViewModel::class.java]
        binding.btnLogout.setOnClickListener {
            loginViewModel.logout()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}