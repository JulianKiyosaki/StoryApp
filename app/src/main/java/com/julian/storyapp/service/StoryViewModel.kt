package com.julian.storyapp.service

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.launch

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    val stories: LiveData<PagingData<Story>> = repository.getAllStories().cachedIn(viewModelScope)

    private val _storyDetail = MutableLiveData<StoryDetail>()
    val storyDetail: LiveData<StoryDetail> = _storyDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    fun fetchStoryDetail(storyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getStoryDetail(storyId)
                _storyDetail.value = response.story
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching story detail: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

