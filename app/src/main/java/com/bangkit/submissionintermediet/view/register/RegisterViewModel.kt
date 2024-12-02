package com.bangkit.submissionintermediet.view.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.repository.Repository
import com.bangkit.submissionintermediet.response.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    private val _registerResult = MutableStateFlow<Results<RegisterResponse>>(Results.Loading)
    val registerResult = _registerResult.asStateFlow()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = repository.register(name, email, password)
        }
    }
}
