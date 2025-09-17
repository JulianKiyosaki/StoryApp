package com.julian.storyapp.service

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterResponse(
    val error: Boolean,
    val message: String
)

data class LoginResponse(
    val error: Boolean,
    val message: String,
    @SerializedName("loginResult")
    val loginResult: LoginResult
)

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)

data class StoriesResponse(
    val error: Boolean,
    val message: String,
    @SerializedName("listStory")
    val listStory: List<Story>
)

@Entity(tableName = "stories")
data class Story(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("createdAt")
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
)

data class ErrorResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null
)

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    @SerializedName("story")
    val story: StoryDetail
)

data class StoryDetail(
    val id: String,
    val name: String,
    val description: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("createdAt")
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
)