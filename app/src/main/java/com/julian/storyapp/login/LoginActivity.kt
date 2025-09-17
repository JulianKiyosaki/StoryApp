package com.julian.storyapp.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.android.material.snackbar.Snackbar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.julian.storyapp.MainActivity
import com.julian.storyapp.R
import com.julian.storyapp.UserPreference
import com.julian.storyapp.dataStore
import com.julian.storyapp.service.ApiConfig
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(ApiConfig.getApiService("token_placeholder"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val bounceAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.bounce_loop)
        imageView.startAnimation(bounceAnimation)

        val emailEditText = findViewById<TextInputEditText>(R.id.ed_login_email)
        val passwordEditText = findViewById<TextInputEditText>(R.id.ed_login_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            progressBar.visibility = View.VISIBLE

            viewModel.loginUser(email, password) { success, token ->
                progressBar.visibility = View.GONE
                if (success) {
                    lifecycleScope.launch {
                        val userPreference = UserPreference.getInstance(applicationContext.dataStore)
                        userPreference.saveUserToken(token)
                    }

                    Snackbar.make(findViewById(android.R.id.content), "Login Success", Snackbar.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Login failed: $token", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AuthSelectionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
