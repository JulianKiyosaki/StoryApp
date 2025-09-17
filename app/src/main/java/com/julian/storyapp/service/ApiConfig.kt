package com.julian.storyapp.service

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    fun getApiService(token: String): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val response = chain.proceed(req)
            val responseBody = response.body?.string()
            Log.d("ApiConfig", "Response Body: $responseBody")
            response.newBuilder().body(responseBody?.toResponseBody(response.body?.contentType())).build()
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> {
        return try {
            val result = call()
            Result.success(result)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("ApiConfig", "Error HTTP ${e.code()}: $errorBody")
            Result.failure(Exception("API Error: $errorBody"))
        } catch (e: Exception) {
            Log.e("ApiConfig", "General Error: ${e.message}")
            Result.failure(e)
        }
    }

}
