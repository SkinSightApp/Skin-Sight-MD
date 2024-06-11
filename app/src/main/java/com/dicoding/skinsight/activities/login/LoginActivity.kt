package com.dicoding.skinsight.activities.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.skinsight.R
import com.dicoding.skinsight.activities.home.HomeActivity
import com.dicoding.skinsight.activities.register.RegisterActivity
import com.dicoding.skinsight.databinding.ActivityLoginBinding
import com.dicoding.skinsight.models.MainViewModel
import com.dicoding.skinsight.models.login.LoginViewModel
import com.dicoding.skinsight.models.login.LoginViewModelFactory
import com.dicoding.skinsight.networking.response.LoginDataAccount
import com.dicoding.skinsight.preferences.UserPreference


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleLoginResponse()
        handleLogin()
        handelMoveToRegister()
        handleShowPassword()
    }

    private fun handleLogin(){
        mainViewModel.isLoadingLogin.observe(this) { loading ->
            handelLoading(loading)
        }

        binding.btnLogin.setOnClickListener {
            Log.d("LoginActivity", "Login button clicked")
            binding.CVEmail.clearFocus()
            binding.CVPasswordLogin.clearFocus()
            if (isDataValid()) {
                val requestLogin = LoginDataAccount(
                    binding.CVEmail.text.toString().trim(),
                    binding.CVPasswordLogin.text.toString().trim()
                )
                mainViewModel.getResponseLogin(requestLogin)
            } else {
                if (!binding.CVEmail.isEmailValid) binding.CVEmail.error = "Invalid Email"
                if (!binding.CVPasswordLogin.isPasswordValid) binding.CVPasswordLogin.error =
                    "Invalid Password"
            }
        }
    }

    private fun handleLoginResponse() {
        val preferences = UserPreference.getInstance(dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(preferences))[LoginViewModel::class.java]

        mainViewModel.statusLogin.observe(this) { status ->
            if (status == "success"){
                mainViewModel.userLogin.value?.let { showToast(it.message) }
                loginViewModel.saveLoginSession(true)
                loginViewModel.saveToken(mainViewModel.userLogin.value!!.data.token)

                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }else{
                showToast("Credentials are invalid")
            }
        }

    }


    private fun handelMoveToRegister(){
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleShowPassword() {
        binding.showPassword.setOnCheckedChangeListener { _, isChecked ->
            val transformationMethod =
                if (isChecked) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
            binding.CVPasswordLogin.transformationMethod = transformationMethod
        }

    }
    private fun isDataValid(): Boolean {
        return binding.CVEmail.isEmailValid && binding.CVPasswordLogin.isPasswordValid
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handelLoading(loading : Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.text = if (loading) "" else "Login"
    }
}