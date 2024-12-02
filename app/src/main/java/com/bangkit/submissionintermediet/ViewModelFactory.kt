package com.bangkit.submissionintermediet

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.submissionintermediet.di.Injection
import com.bangkit.submissionintermediet.repository.Repository
import com.bangkit.submissionintermediet.view.addstrory.AddStoryViewModel
import com.bangkit.submissionintermediet.view.detail.DetailViewModel
import com.bangkit.submissionintermediet.view.home.HomeViewModel
import com.bangkit.submissionintermediet.view.login.LoginViewModel
import com.bangkit.submissionintermediet.view.register.RegisterViewModel

val Context.dataStore by preferencesDataStore(name = "user_preferences")

class ViewModelFactory private constructor(
    private val repository: Repository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        RegisterViewModel::class.java -> RegisterViewModel(repository) as T
        LoginViewModel::class.java -> LoginViewModel(repository) as T
        HomeViewModel::class.java -> HomeViewModel(repository) as T
        DetailViewModel::class.java -> DetailViewModel(repository) as T
        AddStoryViewModel::class.java -> AddStoryViewModel(repository) as T
        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideAppRepository(context)
                )
            }.also { instance = it }
    }
}
