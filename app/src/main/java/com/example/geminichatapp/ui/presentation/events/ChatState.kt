package com.example.geminichatapp.ui.presentation.events

import android.graphics.Bitmap
import com.example.geminichatapp.data.database.ChatDatabase
import com.example.geminichatapp.data.model.Chat
import com.example.geminichatapp.data.model.ChatHistory

data class ChatState(
    val chatList : MutableList<Chat> = mutableListOf(),
    val prompt : String = "",
    val bitmap: Bitmap? = null
)


data class ChatDatabaseState(
    val chatDatabaseList : MutableList<ChatHistory> = mutableListOf(),
    )
