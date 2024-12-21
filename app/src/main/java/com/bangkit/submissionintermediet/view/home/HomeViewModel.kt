package com.bangkit.submissionintermediet.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.submissionintermediet.pagging.StoryEntity
import com.bangkit.submissionintermediet.repository.Repository

class HomeViewModel(repository: Repository) : ViewModel() {
    val getAllStory: LiveData<PagingData<StoryEntity>> = repository.getPagingStory()
        .cachedIn(viewModelScope)
}
