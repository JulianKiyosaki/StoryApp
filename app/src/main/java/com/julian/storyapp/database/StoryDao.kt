package com.julian.storyapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.julian.storyapp.service.Story

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<Story>)

    @Query("SELECT * FROM stories")
    fun getStories(): PagingSource<Int, Story>

    @Query("DELETE FROM stories")
    suspend fun clearStories()
}
