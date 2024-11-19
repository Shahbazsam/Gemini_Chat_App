package com.example.geminichatapp.ui.presentation.screens

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.geminichatapp.ChatApplication
import com.example.geminichatapp.data.model.Chat
import com.example.geminichatapp.data.model.ChatHistory
import com.example.geminichatapp.data.repository.ChatData
import com.example.geminichatapp.data.repository.ChatsRepository
import com.example.geminichatapp.ui.presentation.events.ChatDatabaseState
import com.example.geminichatapp.ui.presentation.events.ChatState
import com.example.geminichatapp.ui.presentation.events.ChatUiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ChatViewModel(private val chatsRepository: ChatsRepository) : ViewModel() {

    val chatData = ChatData()
    private var latestConversationId :Int = 0

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    private val _chatDatabaseState = MutableStateFlow(ChatDatabaseState())
    val chatDatabaseState = _chatDatabaseState.asStateFlow()

    init {
        viewModelScope.launch {
            val ConversationId = chatsRepository.getLatestConversationId()
            if(ConversationId == null){
                latestConversationId = 0
            }else {
                latestConversationId = ConversationId + 1
            }
        }
    }


    fun onEvent(event: ChatUiEvent) {
        when(event) {
            is ChatUiEvent.SendPrompt -> {


                addPromptToChatList(event.prompt , event.bitmap)
                val byteArray = bitmapToByteArray(event.bitmap)
                addPromptToChatDatabase(event.prompt , byteArray)
                if (event.bitmap != null) {
                    getResponseByPromptAndBitmap(event.prompt , event.bitmap)
                }else {
                    getResponseByPrompt(event.prompt)
                }
            }
            is ChatUiEvent.UpdateBitmap -> {
                _chatState.update {
                    it.copy(
                        bitmap = event.newBitmap
                    )
                }

            }
            is ChatUiEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(
                        prompt = event.newPrompt
                    )
                }
            }
        }
    }
    private fun addPromptToChatDatabase(prompt : String , bytearray : ByteArray?) {
        val chatHistory = ChatHistory(
            conversationId = latestConversationId,
            prompt = prompt,
            byteArray = bytearray,
            isFromUser = true
        )
        viewModelScope.launch {
            chatsRepository.insertChat(chatHistory)
        }
    }
    private fun addPromptToChatList(prompt : String , bitmap: Bitmap?) {
        _chatState.update {
           it.copy(
               chatList = it.chatList.toMutableList().apply {
                   add(0 , Chat(prompt , bitmap , true))
               },
               prompt = "",
               bitmap = null
           )
        }
    }
    private fun getResponseByPrompt(prompt : String) {
        viewModelScope.launch {
            val chat = chatData.getResponseByPrompt(prompt)
            val response = parseText(chat)
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0 , Chat(response , null, false))
                    }
                )
            }
            val chatHistory = ChatHistory(
                conversationId = latestConversationId,
                prompt = response,
                byteArray = null,
                isFromUser = false
            )
            chatsRepository.insertChat(chatHistory)

        }
    }


    private fun getResponseByPromptAndBitmap(prompt: String  , bitmap: Bitmap) {
        viewModelScope.launch {
            val chat = chatData.getResponseByPromptAndBitmap(prompt , bitmap)
            val response = parseText(chat)
            _chatState.update {
                it.copy(
                   chatList = it.chatList.toMutableList().apply {
                       add(0 , Chat(response , null, false))
                   }
                )
            }
            val chatHistory = ChatHistory(
                conversationId = latestConversationId,
                prompt = response,
                byteArray = null,
                isFromUser = false
            )
            chatsRepository.insertChat(chatHistory)
        }
    }
    private fun parseText(rawText : String) : String {
        return rawText
            .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1") // Remove double asterisks (**bold text** -> bold text)
            .replace(Regex("\\* ([^\\n]+)"), "• $1")     // Convert single * list items to bullet points
            .replace(Regex("\\[([^\\]]+)]\\(([^)]+)\\)"), "$1 ($2)") // Format links [text](link) -> text (link)
            .trim()
    }

    private fun bitmapToByteArray(bitmap: Bitmap?) : ByteArray? {
        if (bitmap == null )
        {
            return null
        }
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG , 100 , outputStream)
        return outputStream.toByteArray()
    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ChatApplication)
                ChatViewModel(application.container.chatsRepository)
            }
        }
    }
}