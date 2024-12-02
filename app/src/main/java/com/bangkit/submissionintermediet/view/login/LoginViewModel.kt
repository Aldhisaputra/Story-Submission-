package com.bangkit.submissionintermediet.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.repository.Repository
import com.bangkit.submissionintermediet.response.LoginResult
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {

    fun login(email: String, password: String, onSuccess: (LoginResult) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val result = repository.login(email, password)) {
                is Results.Success -> result.data.loginResult?.let {
                    repository.saveToken(it.token.orEmpty())
                    onSuccess(it)
                } ?: onError("Login failed: no results")
                is Results.Error -> onError(result.error)
                else -> Unit
            }
        }
    }
}
