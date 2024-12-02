package com.bangkit.submissionintermediet.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.repository.Repository
import com.bangkit.submissionintermediet.response.Story

class DetailViewModel(private val repository: Repository) : ViewModel() {

    fun getDetailStory(storyId: String): LiveData<Results<Story>> =
        repository.getDetailStory(storyId)
}
