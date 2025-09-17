package com.julian.storyapp.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.julian.storyapp.Result

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {

    fun uploadStory(photo: MultipartBody.Part, description: RequestBody): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = repository.uploadStory(photo, description)
            if (response.error) {
                emit(Result.Error(Exception(response.message)))
                Log.e("AddStoryViewModel", "Upload failed: ${response.message}")
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            Log.e("AddStoryViewModel", "Upload error", e)
            emit(Result.Error(e))
        }
    }
}
