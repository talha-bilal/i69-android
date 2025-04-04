package com.i69.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.i69.chat.model.ModelQBChatDialogs
import com.i69.chat.dao.ChatDialogsDao
import com.i69.data.models.Moment
import com.i69.data.models.OfflineStory
import com.i69.data.models.User
import com.i69.data.remote.responses.DefaultPicker
import com.i69.profile.db.converters.PickerConverters
import com.i69.profile.db.converters.UserConverters
import com.i69.profile.db.dao.UserDao
import com.i69.ui.screens.main.moment.db.MomentDao

@Database(entities = [User::class, Moment::class, DefaultPicker::class, OfflineStory::class, ModelQBChatDialogs::class], version = 25, exportSchema = false)
@TypeConverters(UserConverters::class, PickerConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun momentDao(): MomentDao
    abstract fun userDao(): UserDao

    abstract fun chatDialogDao(): ChatDialogsDao
}