package com.bangkit.submissionintermediet.di

import android.content.Context
import com.bangkit.submissionintermediet.api.ApiConfig
import com.bangkit.submissionintermediet.dataStore
import com.bangkit.submissionintermediet.preference.UserPreference
import com.bangkit.submissionintermediet.repository.Repository

object Injection {
    fun provideAppRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(context.dataStore)
        return Repository.getInstance(apiService, pref)
    }
}