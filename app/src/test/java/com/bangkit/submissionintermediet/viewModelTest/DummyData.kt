package com.bangkit.submissionintermediet.viewModelTest

import com.bangkit.submissionintermediet.pagging.StoryEntity
import java.util.UUID
import kotlin.random.Random

object DummyData {

    fun createSampleStory(): List<StoryEntity> {
        val storyList = mutableListOf<StoryEntity>()
        repeat(101) { index ->
            val storyItem = StoryEntity(
                photoUrl = "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png$index",
                createdAt = "2024",
                name = "User $index",
                description = "Sample description for story number $index",
                lon = Random.nextDouble(-180.0, 180.0),
                id = UUID.randomUUID().toString(),
                lat = Random.nextDouble(-90.0, 90.0)
            )
            storyList.add(storyItem)
        }
        return storyList
    }
}