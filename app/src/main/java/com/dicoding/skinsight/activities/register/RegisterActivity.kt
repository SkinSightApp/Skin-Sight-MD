package com.dicoding.skinsight.activities.register

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.skinsight.R
import com.dicoding.skinsight.activities.home.HomeActivity
import com.dicoding.skinsight.activities.login.dataStore
import com.dicoding.skinsight.databinding.ActivityRegisterBinding
import com.dicoding.skinsight.models.MainViewModel
import com.dicoding.skinsight.models.login.LoginViewModel
import com.dicoding.skinsight.models.login.LoginViewModelFactory
import com.dicoding.skinsight.networking.response.LoginDataAccount
import com.dicoding.skinsight.networking.response.RegisterDataAccount
import com.dicoding.skinsight.preferences.UserPreference

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        handleRegister()
        handleShowPassword()

    }

    private fun setupObservers() {
        val pref = UserPreference.getInstance(dataStore)
        val userLoginViewModel =
            ViewModelProvider(this, LoginViewModelFactory(pref))[LoginViewModel::class.java]
        userLoginViewModel.getLoginSession().observe(this) { sessionTrue ->
            if (sessionTrue) {
                val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        mainViewModel.statusRegister.observe(this) { statusRegister ->
            handleRegisterResponse(
                mainViewModel.isErrorRegist,
                statusRegister
            )
        }
//        mainViewModel.isLoadingRegister.observe(this) {
//            displayLoading(it)
//        }
        mainViewModel.statusLogin.observe(this) { statusLogin ->
            handleLoginResponse(
                mainViewModel.isErrorLogin,
                statusLogin,
                userLoginViewModel
            )
        }
//        mainViewModel.isLoadingLogin.observe(this) {
//            displayLoading(it)
//        }
    }

    private fun handleLoginResponse(
        isError: Boolean,
        status: String,
        userLoginViewModel: LoginViewModel
    ) {
        if (!isError) {
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
            userLoginViewModel.saveLoginSession(true)
            userLoginViewModel.saveToken(mainViewModel.userLogin.value!!.data.token)
        } else {
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleRegister(){
        binding.btnRegisterAccount.setOnClickListener {
            binding.cvName.clearFocus()
            binding.cvEmail.clearFocus()
            binding.cvPassword.clearFocus()
            binding.cvConfirmPassword.clearFocus()

            if (binding.cvName.isNameValid && binding.cvEmail.isEmailValid && binding.cvPassword.isPasswordValid && binding.cvConfirmPassword.isPasswordValid) {
                val dataRegisterAccount = RegisterDataAccount(
                    name = binding.cvName.text.toString().trim(),
                    email = binding.cvEmail.text.toString().trim(),
                    password = binding.cvPassword.text.toString().trim()
                )

                mainViewModel.getResponseRegister(dataRegisterAccount)
            } else {
                if (!binding.cvName.isNameValid) binding.cvName.error = "Name Cannot Be Empty"
//                    getString(R.string.emptyName)
                if (!binding.cvEmail.isEmailValid) binding.cvEmail.error = "Invalid Email"
//                    getString(R.string.invalidEmailFormat)
                if (!binding.cvPassword.isPasswordValid) binding.cvPassword.error = "Password Must More Than Eight Character"
                    getString(R.string.mustMoreThanEightCharacter)
                if (!binding.cvConfirmPassword.isPasswordValid) binding.cvConfirmPassword.error = "Password Does Not Match"
//                    getString(R.string.passwordCannotbeEmpty)
                Toast.makeText(this, "Please enter Data Correctly", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun handleRegisterResponse(isError: Boolean, message: String) {
        if (isError) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val userLogin = LoginDataAccount(
                binding.cvEmail.text.toString(),
                binding.cvPassword.text.toString()
            )
            mainViewModel.getResponseLogin(userLogin)
        }
    }
    private fun handleShowPassword() {
        binding.showPassword.setOnCheckedChangeListener { _, isChecked ->
            val transformationMethod =
                if (isChecked) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
            binding.cvPassword.transformationMethod = transformationMethod
            binding.cvConfirmPassword.transformationMethod = transformationMethod
        }

    }
    private fun isDataValid(): Boolean {
        return binding.cvEmail.isEmailValid && binding.cvPassword.isPasswordValid
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}