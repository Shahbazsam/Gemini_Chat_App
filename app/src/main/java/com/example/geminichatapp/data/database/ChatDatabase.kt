package com.example.geminichatapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.geminichatapp.data.dao.ChatDao
import com.example.geminichatapp.data.model.ChatHistory


@Database(
    entities = [
        ChatHistory::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var Instance : ChatDatabase? = null

        fun getDatabase(context: Context): ChatDatabase {

            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context , ChatDatabase::class.java , "chat_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        Instance = it
                    }
            }
        }
    }
}