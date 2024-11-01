package com.example.geminichatapp

import android.graphics.Bitmap
import com.example.geminichatapp.data.Chat

data class ChatState(
    val chatList : MutableList<Chat> = mutableListOf(),
    val prompt : String = "",
    val bitmap: Bitmap? = null
)
