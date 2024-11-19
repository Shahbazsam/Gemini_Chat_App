package com.example.geminichatapp.data.container

import android.content.Context
import com.example.geminichatapp.data.database.ChatDatabase
import com.example.geminichatapp.data.repository.ChatsRepository
import com.example.geminichatapp.data.repository.ChatsRepositoryImpl

interface AppContainer {
    val chatsRepository: ChatsRepository
}

class AppContainerImpl(private val context: Context) : AppContainer {
    override val chatsRepository: ChatsRepository by lazy {
        ChatsRepositoryImpl(
            ChatDatabase.getDatabase(context).chatDao()
        )
    }

}