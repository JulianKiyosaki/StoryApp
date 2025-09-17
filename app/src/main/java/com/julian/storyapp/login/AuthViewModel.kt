package com.julian.storyapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julian.storyapp.service.ApiService
import com.julian.storyapp.service.LoginRequest
import com.julian.storyapp.service.RegisterRequest
import kotlinx.coroutines.launch

class AuthViewModel(private val apiService: ApiService) : ViewModel() {

    fun registerUser(name: String, email: String, password: String, callback: (String) -> Unit) {
        val request = RegisterRequest(name, email, password)
        viewModelScope.launch {
            try {
                val response = apiService.register(request)
                if (response.error) {
                    if (response.message.contains("Already Registered", ignoreCase = true)) {
                        callback("Account Already Registered")
                    } else {
                        callback(response.message)
                    }
                } else {
                    callback("User Created")
                }
            } catch (e: Exception) {
                callback("Registration failed: ${e.message}")
            }
        }
    }


    fun loginUser(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = apiService.login(loginRequest)

                if (response.error) {
                    onResult(false, "Login failed: ${response.message}")
                } else {
                    onResult(true, response.loginResult.token)
                }
            } catch (e: Exception) {
                onResult(false, "Login failed: ${e.localizedMessage}")
            }
        }
    }
}