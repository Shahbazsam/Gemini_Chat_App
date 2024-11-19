package com.example.geminichatapp.data.repository

import com.example.geminichatapp.data.dao.ChatDao
import com.example.geminichatapp.data.model.Chat
import com.example.geminichatapp.data.model.ChatHistory
import kotlinx.coroutines.flow.Flow

interface ChatsRepository {

    suspend fun insertChat(chat: ChatHistory)
    suspend fun getLatestConversationId(): Int?
    fun getChatsForConversation(conversationId: Long): Flow<List<ChatHistory>>
    suspend fun deleteChatsForConversation(conversationId: Long)
    suspend fun deleteSingleMessage(conversationID : Long ,chatId: Long)


}

 class ChatsRepositoryImpl(
     private val chatDao: ChatDao,
) : ChatsRepository {

     override suspend fun insertChat(chat: ChatHistory) {
         chatDao.insertChat(chat)
     }
     override suspend fun getLatestConversationId(): Int? {
         return chatDao.getLatestConversationId()
     }

     override fun getChatsForConversation(conversationId: Long): Flow<List<ChatHistory>> {
         return chatDao.getChatsForConversation(conversationId)
     }

     override suspend fun deleteChatsForConversation(conversationId: Long) {
         chatDao.deleteChatsForConversation(conversationId)
     }

     override suspend fun deleteSingleMessage(conversationID: Long, chatId: Long) {
         chatDao.deleteSingleMessage(conversationID , chatId)
     }


 }