package com.i69.ui.screens.main.moment.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.i69.data.models.Moment
import com.i69.data.models.OfflineStory

@Dao
interface MomentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMomentsList(moments: List<Moment>)

    @Query("SELECT * FROM moment_table")
    fun getMomentsList(): List<Moment>

    @Query("DELETE FROM moment_table")
    fun deleteAllMoments()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStoryList(moments: List<OfflineStory>)

    @Query("SELECT * FROM story_table")
    fun getStoryList(): List<OfflineStory>

    @Query("DELETE FROM story_table")
    fun deleteAllStories()
}