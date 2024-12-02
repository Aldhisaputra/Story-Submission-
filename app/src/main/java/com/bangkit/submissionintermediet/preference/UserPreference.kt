package com.bangkit.submissionintermediet.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private val tokenKey = stringPreferencesKey("token")

    suspend fun saveToken(token: String) {
        dataStore.edit { it[tokenKey] = token }
    }

    fun getToken(): Flow<String?> = dataStore.data.map { it[tokenKey] }

    suspend fun clearToken() {
        dataStore.edit { it.remove(tokenKey) }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreference(dataStore).also { INSTANCE = it }
            }
    }
}
