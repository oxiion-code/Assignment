package com.meow.cosmos.database.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java,
            "chat_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideChatDao(database: ChatDatabase): ChatDao {
        return database.chatDao()
    }


}
