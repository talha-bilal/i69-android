package com.i69.data.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.i69.GetAllUserMomentsQuery
import com.i69.GetAllUserMultiStoriesQuery
import com.i69.GetUserMomentsQuery

@Keep
@Entity(tableName = "story_table")
data class OfflineStory(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Int? = null,
    var stories: GetAllUserMultiStoriesQuery.AllUserMultiStory?
)
