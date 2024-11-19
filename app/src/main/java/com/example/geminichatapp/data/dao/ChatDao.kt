package com.example.geminichatapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.geminichatapp.data.model.ChatHistory
import com.example.geminichatapp.data.repository.ChatData
import kotlinx.coroutines.flow.Flow


@Dao
interface ChatDao {

    @Insert
    suspend fun insertChat(chatDatabase: ChatHistory)

    @Query("SELECT MAX(conversationId) FROM chats")
    suspend fun getLatestConversationId(): Int?

    @Query("SELECT * FROM chats WHERE conversationId = :conversationId ORDER BY id ")
    fun getChatsForConversation(conversationId: Long): Flow<List<ChatHistory>>

    @Query("DELETE FROM chats WHERE conversationId = :conversationId")
    suspend fun deleteChatsForConversation(conversationId: Long)

    @Query("DELETE FROM chats WHERE conversationId = :conversationId AND id = :chatId")
    suspend fun deleteSingleMessage(conversationId: Long, chatId: Long)

}
