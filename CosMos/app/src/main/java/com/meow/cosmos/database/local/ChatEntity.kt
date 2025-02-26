package com.meow.cosmos.database.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Unique ID for each message
    val senderIsApp: Boolean = true,
    val message: String = "",
    val localResponse:String="",
    val aiResponse: String = "",
    val timestamp: Long = System.currentTimeMillis() // Store the timestamp
)

