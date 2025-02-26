package com.meow.cosmos.data.repositories

import com.meow.cosmos.database.local.ChatEntity

interface ChatRepository {
    suspend fun insertChat(chat: ChatEntity)
    suspend fun getAllChats(): List<ChatEntity>
    suspend fun getLastChat(): ChatEntity?
    suspend fun clearHistory():Boolean
}