package com.julian.storyapp.login

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.julian.storyapp.R
import com.julian.storyapp.databinding.ActivityRegisterBinding
import com.julian.storyapp.service.ApiConfig

class RegisterActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(ApiConfig.getApiService("token_placeholder"))
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val bounceAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.bounce_loop)
        imageView.startAnimation(bounceAnimation)

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            binding.progressBar.visibility = View.VISIBLE

            viewModel.registerUser(name, email, password) { message ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
