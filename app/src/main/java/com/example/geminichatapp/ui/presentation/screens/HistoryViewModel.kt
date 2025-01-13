package com.example.geminichatapp.ui.presentation.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.geminichatapp.ChatApplication
import com.example.geminichatapp.data.repository.ChatsRepository
import com.example.geminichatapp.ui.presentation.events.ChatDatabaseState
import com.example.geminichatapp.ui.presentation.events.ChatHistoryListDatabaseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val chatsRepository : ChatsRepository
) : ViewModel() {

    private val _listOfConversationState = MutableStateFlow(ChatDatabaseState())
    val listOfConversationState = _listOfConversationState.asStateFlow()

    private val _historyOfConversationState = MutableStateFlow(ChatHistoryListDatabaseState())
    val historyOfConversationState = _historyOfConversationState.asStateFlow()



    init {
        getSortedConversation()
    }

    private fun getSortedConversation() {
        viewModelScope.launch {
            chatsRepository.getAllConversationsSorted()
                .map { chats ->
                    chats.groupBy { it.conversationId }
                        .map { entry -> entry.value.lastOrNull() }
                        .filterNotNull()
                        .sortedByDescending { it.conversationId }
                }
                .collect { sortedConversation ->
                    _listOfConversationState.update {
                            it.copy(
                                chatDatabaseList = sortedConversation
                            )
                    }
                }
        }
    }

    fun getHistoryByConversationId(conversationId : Int) {
        viewModelScope.launch {
           chatsRepository.getChatsForConversation(conversationId).collect {chat ->
               Log.d("ChatDebug", "Emitted chat list: $chat")
               _historyOfConversationState.update {
                   it.copy(
                       chatHistoryForConversation = chat
                   )
               }
               Log.d("ChatDebug1", _historyOfConversationState.value.chatHistoryForConversation.toString())
           }
        }
    }


    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ChatApplication)
                HistoryViewModel(application.container.chatsRepository)
            }
        }
    }
}