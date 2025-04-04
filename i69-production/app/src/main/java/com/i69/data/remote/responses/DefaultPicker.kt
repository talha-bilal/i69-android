package com.i69.data.remote.responses

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.i69.data.models.IdWithValue
import com.i69.profile.db.converters.UserConverters

@Keep
@Entity(tableName = "picker_table")
data class DefaultPicker(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    @TypeConverters(UserConverters::class)
    var agePicker: List<IdWithValue> = emptyList(),
    @TypeConverters(UserConverters::class)
    var ethnicityPicker: List<IdWithValue> = emptyList(),
    @TypeConverters(UserConverters::class)
    var familyPicker: List<IdWithValue> = emptyList(),
    @TypeConverters(UserConverters::class)
    var heightsPicker: List<IdWithValue> = emptyList(),
    @TypeConverters(UserConverters::class)
    var politicsPicker: List<IdWithValue> = emptyList(),
    @TypeConverters(UserConverters::class)
    var religiousPicker: List<IdWithValue> = emptyList(),
    @TypeConverters(UserConverters::class)
    var tagsPicker: List<IdWithValue> = emptyList(),
    @TypeConverters(UserConverters::class)
    var zodiacSignPicker: List<IdWithValue> = emptyList(),
    @TypeConverters(UserConverters::class)
    var genderPicker: List<IdWithValue> = emptyList()
)