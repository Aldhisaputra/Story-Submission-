package com.bangkit.submissionintermediet.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.repository.Repository
import com.bangkit.submissionintermediet.response.ListStoryItem

class HomeViewModel(repository: Repository) : ViewModel() {
    val getAllStories: LiveData<Results<List<ListStoryItem>>> = repository.getAllStories()
}
