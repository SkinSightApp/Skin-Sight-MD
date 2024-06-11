package com.dicoding.skinsight

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.skinsight.activities.home.HomeActivity
import com.dicoding.skinsight.activities.login.LoginActivity
import com.dicoding.skinsight.activities.login.dataStore
import com.dicoding.skinsight.activities.register.RegisterActivity
import com.dicoding.skinsight.databinding.ActivityMainBinding
import com.dicoding.skinsight.databinding.SplashScreenBinding
import com.dicoding.skinsight.models.MainViewModel
import com.dicoding.skinsight.models.login.LoginViewModel
import com.dicoding.skinsight.models.login.LoginViewModelFactory
import com.dicoding.skinsight.preferences.UserPreference

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var splashBinding: SplashScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        splashBinding = SplashScreenBinding.inflate(layoutInflater)

        handleSplashScreen()
    }

    private fun handleSplashScreen() {
        val preferences = UserPreference.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory(preferences))[LoginViewModel::class.java]

        loginViewModel.getLoginSession().observe(this) { sessionExists ->
            if (sessionExists) {
                setContentView(splashBinding.root)
                splashBinding.splashScreen.setTransitionListener(object :
                    MotionLayout.TransitionListener {
                    override fun onTransitionStarted(
                        motionLayout: MotionLayout?,
                        startId: Int,
                        endId: Int
                    ) {
                    }

                    override fun onTransitionChange(
                        motionLayout: MotionLayout?,
                        startId: Int,
                        endId: Int,
                        progress: Float
                    ) {

                    }

                    override fun onTransitionCompleted(
                        motionLayout: MotionLayout?,
                        currentId: Int
                    ) {
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }

                    override fun onTransitionTrigger(
                        motionLayout: MotionLayout?,
                        triggerId: Int,
                        positive: Boolean,
                        progress: Float
                    ) {

                    }
                })

            } else {
                setContentView(binding.root)
                binding.main.setTransitionListener(object : MotionLayout.TransitionListener {
                    override fun onTransitionStarted(
                        motionLayout: MotionLayout?,
                        startId: Int,
                        endId: Int
                    ) {
                    }

                    override fun onTransitionChange(
                        motionLayout: MotionLayout?,
                        startId: Int,
                        endId: Int,
                        progress: Float
                    ) {

                    }

                    override fun onTransitionCompleted(
                        motionLayout: MotionLayout?,
                        currentId: Int
                    ) {

                    }

                    override fun onTransitionTrigger(
                        motionLayout: MotionLayout?,
                        triggerId: Int,
                        positive: Boolean,
                        progress: Float
                    ) {

                    }
                })
            }
        }
        handelButton()
    }


    private fun handelButton() {
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}