package com.julian.storyapp.service

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.julian.storyapp.UserPreference
import com.julian.storyapp.database.StoryDatabase
import com.julian.storyapp.data.StoryRemoteMediator
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val pref: UserPreference,
    private val storyDatabase: StoryDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getAllStories(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, pref),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories() }
        ).liveData
    }


    suspend fun getStoryDetail(storyId: String): StoryDetailResponse {
        val token = pref.getUserToken().firstOrNull() ?: ""

        if (token.isEmpty()) {
            throw Exception("Token is empty. Authentication might be required.")
        }

        return apiService.detailStory(storyId, "Bearer $token")
    }


    suspend fun uploadStory(photo: MultipartBody.Part, description: RequestBody): RegisterResponse {
        val token = pref.getUserToken().firstOrNull() ?: ""
        return apiService.addStory(description, photo, "Bearer $token")
    }

    suspend fun getStoriesWithLocation(): StoriesResponse {
        val token = pref.getUserToken().firstOrNull() ?: ""
        if (token.isEmpty()) throw Exception("Token null. Please Login again!")

        return apiService.getStoriesWithLocation("Bearer $token", 1)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, pref: UserPreference, storyDatabase: StoryDatabase): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, pref, storyDatabase).also { instance = it }
            }
    }
}
