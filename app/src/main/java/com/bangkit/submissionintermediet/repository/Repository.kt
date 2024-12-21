package com.bangkit.submissionintermediet.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit.submissionintermediet.api.ApiService
import com.bangkit.submissionintermediet.Results
import com.bangkit.submissionintermediet.pagging.StoryMediator
import com.bangkit.submissionintermediet.pagging.StoryDatabase
import com.bangkit.submissionintermediet.pagging.StoryEntity
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

open class Repository(
    private val apiService: ApiService,
    private val preference: UserPreference,
    private val database: StoryDatabase,
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

    fun getPagingStory(): LiveData<PagingData<StoryEntity>> = liveData {
        val token = preference.getToken().first()
        @OptIn(ExperimentalPagingApi::class)
        emitSource(
            Pager(
                config = PagingConfig(pageSize = 5),
                remoteMediator = token?.let { StoryMediator(database, apiService, it) },
                pagingSourceFactory = { database.storyDao().getAllStory() }
            ).liveData
        )
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

    fun getAllStoryLocation(): LiveData<Results<List<ListStoryItem>>> = liveData {
        emit(Results.Loading)
        try {
            val token = preference.getToken().first()
            val response = apiService.getLocation(
                token = "Bearer $token",
                location = 1
            )
            emit(Results.Success(response.listStory))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            pref: UserPreference,
            database: StoryDatabase
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, pref, database)
            }.also { instance = it }
    }
}
