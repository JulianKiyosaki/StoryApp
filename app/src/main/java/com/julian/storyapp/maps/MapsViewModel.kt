package com.julian.storyapp.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.julian.storyapp.service.Story
import com.julian.storyapp.service.StoryRepository
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<List<Story>>()
    val storiesWithLocation: LiveData<List<Story>> = _storiesWithLocation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getStoriesWithLocation()
                if (!response.error) {
                    _storiesWithLocation.value = response.listStory
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching stories with location: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
