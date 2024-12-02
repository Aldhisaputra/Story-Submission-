package com.bangkit.submissionintermediet.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit.submissionintermediet.api.ApiService
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.preference.UserPreference
import com.bangkit.submissionintermediet.response.ListStoryItem
import com.bangkit.submissionintermediet.response.LoginResponse
import com.bangkit.submissionintermediet.response.RegisterResponse
import com.bangkit.submissionintermediet.response.Story
import com.bangkit.submissionintermediet.response.StoryUploadResponse
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

class Repository(
    private val apiService: ApiService,
    private val preference: UserPreference
) {
    suspend fun login(email: String, password: String): Results<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            Results.Success(response)
        } catch (e: Exception) {
            Results.Error(e.message ?: "Terjadi kesalahan")
        }
    }

    suspend fun saveToken(token: String) {
        preference.saveToken(token)
    }

    suspend fun register(name: String, email: String, password: String): Results<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            Results.Success(response)
        } catch (e: Exception) {
            Results.Error(e.message ?: "Terjadi kesalahan")
        }
    }

    fun getAllStories(): LiveData<Results<List<ListStoryItem>>> = liveData {
        emit(Results.Loading)
        try {
            val token = preference.getToken().first()
            val response = apiService.getAllStories(
                token = "Bearer $token",
                page = null,
                size = null,
                location = 0
            )
            emit(Results.Success(response.listStory))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    fun getDetailStory(storyId: String): LiveData<Results<Story>> = liveData {
        emit(Results.Loading)
        try {
            val token = preference.getToken().first()
            Log.d("AppRepository", "Bearer token: $token")
            val response = apiService.getDetailStory(
                token = "Bearer $token",
                id = storyId
            )
            Log.d("DetailStory", response.toString())
            val story = response.story
            if (story != null) {
                emit(Results.Success(story))
            } else {
                emit(Results.Error("Cerita tidak ditemukan"))
            }
        } catch (e: Exception) {
            Log.d("DetailStory", e.toString())
            emit(Results.Error(e.message.toString()))
        }
    }

    suspend fun uploadStory(
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody?,
        lon: RequestBody?
    ): Results<StoryUploadResponse> {
        return try {
            val token = preference.getToken().first()
            Log.d("Repository", "Bearer token: $token")
            val response = apiService.uploadStory("Bearer $token", description, photo, lat, lon)

            if (!response.error!!) {
                Results.Success(response)
            } else {
                Results.Error(response.message ?: "Terjadi kesalahan yang tidak diketahui")
            }
        } catch (e: IOException) {
            Results.Error("Kesalahan jaringan: ${e.message}")
        } catch (e: HttpException) {
            Results.Error("Kesalahan HTTP: ${e.message}")
        } catch (e: Exception) {
            Results.Error("Terjadi kesalahan tak terduga: ${e.message}")
        }
    }


    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            pref: UserPreference
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, pref)
            }.also { instance = it }
    }
}