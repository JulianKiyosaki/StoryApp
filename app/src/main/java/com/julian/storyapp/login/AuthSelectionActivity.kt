package com.julian.storyapp.login

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.julian.storyapp.MainActivity
import com.julian.storyapp.R
import com.julian.storyapp.UserPreference
import com.julian.storyapp.dataStore
import com.julian.storyapp.databinding.ActivityAuthSelectionBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageView = binding.imageView
        val moveUpAnimation = AnimationUtils.loadAnimation(this, R.anim.image_move_up)
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce_loop)
        imageView.startAnimation(moveUpAnimation)

        moveUpAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                imageView.startAnimation(bounceAnimation)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        lifecycleScope.launch {
            val token = UserPreference.getInstance(applicationContext.dataStore).getUserToken().first()
            if (!token.isNullOrEmpty()) {
                val intent = Intent(this@AuthSelectionActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
