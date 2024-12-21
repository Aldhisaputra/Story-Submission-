package com.bangkit.submissionintermediet.pagging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bangkit.submissionintermediet.api.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> getRemoteKeyClosestToCurrentPosition(state)?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            LoadType.PREPEND -> getRemoteKeyForFirstItem(state)?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> getRemoteKeyForLastItem(state)?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
        }

        return try {
            val responseData = apiService.getAllStory("Bearer $token", page, state.config.pageSize)
            val endOfPaginationReached = responseData.listStory.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = responseData.listStory.map { story ->
                    RemoteKeys(story.id!!, prevKey, nextKey)
                }
                database.remoteKeysDao().insertAll(keys)

                val stories = responseData.listStory.map { story ->
                    StoryEntity(story.photoUrl, story.createdAt!!, story.name!!, story.description!!, story.lon, story.id!!, story.lat)
                }
                database.storyDao().insertStory(stories)
            }

            MediatorResult.Success(endOfPaginationReached)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>) =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>) =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>) =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
}
