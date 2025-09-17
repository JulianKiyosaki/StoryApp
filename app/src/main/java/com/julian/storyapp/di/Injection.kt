package com.julian.storyapp.di

import android.content.Context
import android.util.Log
import com.julian.storyapp.UserPreference
import com.julian.storyapp.dataStore
import com.julian.storyapp.database.StoryDatabase
import com.julian.storyapp.service.ApiConfig
import com.julian.storyapp.service.StoryRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val token = runBlocking { pref.getUserToken().firstOrNull() ?: "" }
        val storyDatabase = StoryDatabase.getDatabase(context)
        if (token.isEmpty()) {
            Log.e("Injection", "Token is empty. Authentication might be required.")
        }

        val apiService = ApiConfig.getApiService("Bearer $token")
        return StoryRepository.getInstance(apiService, pref,storyDatabase)
    }
}

