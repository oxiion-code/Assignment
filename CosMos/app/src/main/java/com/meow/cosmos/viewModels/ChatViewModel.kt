package com.meow.cosmos.viewModels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.meow.cosmos.data.Constants
import com.meow.cosmos.data.repositories.ChatRepository
import com.meow.cosmos.database.local.ChatEntity
import com.meow.cosmos.models.TarotCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _chatList = MutableStateFlow<List<ChatEntity>>(emptyList())
    val chatList = _chatList.asStateFlow()

    private val _lastChat = MutableStateFlow<ChatEntity?>(null)
    val lastChat = _lastChat.asStateFlow()

    val messageList by lazy {
        mutableStateListOf<String>()
    }

    // States to manage loading, success, and failure
    private val _sendMessageState = MutableStateFlow<SendMessageState>(SendMessageState.Idle)
    val sendMessageState = _sendMessageState.asStateFlow()

    init {
        fetchChats()
    }

    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-001",
        apiKey = Constants.API_KEY
    )

    private fun fetchChats() {
        viewModelScope.launch {
            _chatList.value = repository.getAllChats()
        }
    }

    // Enum class to represent the states of sending a message
    sealed class SendMessageState {
        object Idle : SendMessageState()
        object Loading : SendMessageState()
        data class Success(val response: String) : SendMessageState()
        data class Failure(val errorMessage: String) : SendMessageState()
    }

    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {

                _sendMessageState.value = SendMessageState.Loading

                // Create the chat history for the generative model
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it) {
                            text(it)
                        }
                    }.toList()
                )

                // Send the message to the generative model
                val response = chat.sendMessage(question)
                _sendMessageState.value = SendMessageState.Success(response.text.toString())
            } catch (e: Exception) {
                // In case of an error, show failure with the exception message
                _sendMessageState.value = SendMessageState.Failure(
                    e.message.toString() + " - Check internet connection"
                )
            }
        }
    }

    fun sendMessage(message: String, isFromApp: Boolean, aiResponse: String = "") {
        viewModelScope.launch {
            val chat = ChatEntity(senderIsApp = isFromApp, message = message, aiResponse = aiResponse)
            repository.insertChat(chat)
            _chatList.value = repository.getAllChats() // Directly update the chat list
        }
    }

    fun fetchLastChat() {
        viewModelScope.launch {
            _lastChat.value = repository.getLastChat()
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            val isCleared = repository.clearHistory()
            if (isCleared) {
                _chatList.value = emptyList() // Clear the list in the UI
            }
        }
    }

    fun loadTarotCards(context: Context): List<TarotCard> {
        return try {
            val jsonString = context.assets.open("cards.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<TarotCard>>() {}.type
            Gson().fromJson(jsonString, listType)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }
}
