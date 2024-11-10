package com.example.geminichatapp.ui.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminichatapp.data.ChatData
import com.example.geminichatapp.data.ChatState
import com.example.geminichatapp.data.ChatUiEvent
import com.example.geminichatapp.data.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val chatData = ChatData()

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()



    fun onEvent(event: ChatUiEvent) {
        when(event) {
            is ChatUiEvent.SendPrompt -> {
                addPromptToChatList(event.prompt , event.bitmap)
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
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0 , chat)
                    }
                )
            }

        }
    }

    private fun getResponseByPromptAndBitmap(prompt: String  , bitmap: Bitmap) {
        viewModelScope.launch {
            val chat = chatData.getResponseByPromptAndBitmap(prompt , bitmap)
            _chatState.update {
                it.copy(
                   chatList = it.chatList.toMutableList().apply {
                       add(0 , chat)
                   }
                )
            }
        }
    }
}