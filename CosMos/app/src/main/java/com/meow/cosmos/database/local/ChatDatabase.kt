package com.meow.cosmos.database.local

import androidx.room.Database
import androidx.room.RoomDatabase



@Database(entities = [ChatEntity::class], version = 2, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}

