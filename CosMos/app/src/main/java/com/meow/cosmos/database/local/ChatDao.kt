package com.meow.cosmos.database.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    suspend fun getAllChats(): List<ChatEntity>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastChat(): ChatEntity?

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory() // Function to delete all chat messages
}
