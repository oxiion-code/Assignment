package com.meow.cosmos.data.usecases

import com.meow.cosmos.data.repositories.ChatRepository
import com.meow.cosmos.database.local.ChatDao
import com.meow.cosmos.database.local.ChatEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao
):ChatRepository {

    override suspend fun insertChat(chat: ChatEntity) {
        chatDao.insertChat(chat)
    }

    override suspend fun getAllChats(): List<ChatEntity> {
        return chatDao.getAllChats()
    }

    override suspend fun getLastChat(): ChatEntity? {
        return chatDao.getLastChat()
    }
    override suspend fun clearHistory(): Boolean {
        return try {
            chatDao.clearChatHistory()
            true
        } catch (e: Exception) {
            false
        }
    }
}
