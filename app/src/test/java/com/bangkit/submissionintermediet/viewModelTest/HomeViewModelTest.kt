package com.bangkit.submissionintermediet.viewModelTest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.bangkit.submissionintermediet.adapter.StoryAdapter
import com.bangkit.submissionintermediet.pagging.StoryEntity
import com.bangkit.submissionintermediet.repository.Repository
import com.bangkit.submissionintermediet.view.home.HomeViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule val mainDispatcherRules = MainDispatcherRule()

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    private suspend fun getDiffer(data: PagingData<StoryEntity>) = AsyncPagingDataDiffer(
        diffCallback = StoryAdapter.DIFF_CALLBACK,
        updateCallback = noopListUpdateCallback,
        workerDispatcher = Dispatchers.Main
    ).apply { submitData(data) }

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStory = DummyData.createSampleStory()
        val data = PagingData.from(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>().apply { value = data }

        val repository: Repository = mockk { coEvery { getPagingStory() } returns expectedStory }
        val homeViewModel = HomeViewModel(repository)

        val actualStories = homeViewModel.getAllStory.getOrAwaitValue()
        val differ = getDiffer(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
        coVerify { repository.getPagingStory() }
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val emptyData = PagingData.from(emptyList<StoryEntity>())
        val expectedStories = MutableLiveData<PagingData<StoryEntity>>().apply { value = emptyData }

        val repository: Repository = mockk { coEvery { getPagingStory() } returns expectedStories }
        val homeViewModel = HomeViewModel(repository)

        val actualStories = homeViewModel.getAllStory.getOrAwaitValue()
        val differ = getDiffer(actualStories)

        Assert.assertEquals(0, differ.snapshot().size)
        coVerify { repository.getPagingStory() }
    }
}
