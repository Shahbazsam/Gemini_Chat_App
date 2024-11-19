package com.example.geminichatapp.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey



data class Chat(
    val prompt : String,
    val bitmap : Bitmap?,
    val isFromUser : Boolean
)


@Entity(
    tableName = "chats",
)
data class ChatHistory(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val conversationId : Int = 0,
    val prompt : String,
    val byteArray: ByteArray ?,
    val isFromUser : Boolean
)
