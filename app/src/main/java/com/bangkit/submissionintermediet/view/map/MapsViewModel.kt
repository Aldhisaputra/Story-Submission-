package com.bangkit.submissionintermediet.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.repository.Repository
import com.bangkit.submissionintermediet.response.ListStoryItem

class MapsViewModel(repository: Repository) : ViewModel() {
    val getAllStoryWithLocation: LiveData<Results<List<ListStoryItem>>> = repository.getAllStoryLocation()
}