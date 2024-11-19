package com.example.geminichatapp.data.repository

import android.graphics.Bitmap
import com.example.geminichatapp.BuildConfig
import com.example.geminichatapp.data.model.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface ChatDataFromModel {

    suspend fun getResponseByPrompt( prompt : String) : String
    suspend fun getResponseByPromptAndBitmap( prompt : String , bitmap: Bitmap) : String
}


class ChatData() : ChatDataFromModel {

    val api_key = BuildConfig.GEMINI_API_KEY

    override suspend fun getResponseByPrompt(prompt: String): String {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.0-pro",
            apiKey = api_key
        )

        try {
            val response  = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }
            return (response.text ?: "error").toString()
//            return Chat(
//                prompt = response.text ?: "error",
//                bitmap = null,
//                isFromUser = false
//            )
        }catch (e : Exception) {
            return ("Model  Error : ${e.message ?: "Unknown error"}").toString()
//            return Chat(
//                prompt = "Model error: ${e.message ?: "Unknown error"}",
//                bitmap = null,
//                isFromUser = false
//            )
        }
    }

    override suspend fun getResponseByPromptAndBitmap(prompt: String, bitmap: Bitmap): String {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-pro",
            apiKey = api_key
        )

        val inputContent = content {
            image(bitmap)
            text(prompt)
        }

        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }
            return (response.text ?: "error").toString()
//            return Chat(
//                prompt = response.text ?: "error",
//                bitmap = null,
//                isFromUser = false
//            )
        }catch (e:Exception) {
            return ("Model  Error : ${e.message ?: "Unknown error"}").toString()
//            return Chat(
//                prompt = "Model error: ${e.message ?: "Unknown error"}",
//                bitmap = null,
//                isFromUser = false
//            )
        }
    }

}

