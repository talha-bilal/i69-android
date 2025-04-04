package com.i69.data.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.i69.GetAllUserMomentsQuery
import com.i69.GetUserMomentsQuery

@Keep
@Entity(tableName = "moment_table")
data class Moment(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Int? = null,
    val node: GetAllUserMomentsQuery.Edge?,
    var image: ByteArray? = null
)
