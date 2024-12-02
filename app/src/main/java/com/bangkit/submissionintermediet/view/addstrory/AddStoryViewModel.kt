package com.bangkit.submissionintermediet.view.addstrory

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.repository.Repository
import com.bangkit.submissionintermediet.response.StoryUploadResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: Repository) : ViewModel() {
    val imageUri = MutableLiveData<Uri?>()
    val isLoading = MutableLiveData<Boolean>()
    val uploadResult = MutableLiveData<Results<StoryUploadResponse>>()

    fun uploadStory(description: RequestBody, photo: MultipartBody.Part, lat: RequestBody? = null, lon: RequestBody? = null) {
        viewModelScope.launch {
            isLoading.value = true
            uploadResult.value = repository.uploadStory(description, photo, lat, lon)
            isLoading.value = false
        }
    }
}
