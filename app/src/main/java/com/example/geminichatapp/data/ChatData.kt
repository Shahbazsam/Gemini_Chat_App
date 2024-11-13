package com.example.geminichatapp.data

import android.graphics.Bitmap
import com.example.geminichatapp.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface ChatDataFromModel {

    suspend fun getResponseByPrompt( prompt : String) : Chat
    suspend fun getResponseByPromptAndBitmap( prompt : String , bitmap: Bitmap) : Chat
}


class ChatData() : ChatDataFromModel {

    val api_key = BuildConfig.GEMINI_API_KEY

    override suspend fun getResponseByPrompt(prompt: String): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.0-pro",
            apiKey = api_key
        )

        try {
            val response  = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }catch (e : Exception) {
            return Chat(
                prompt = "Model error: ${e.message ?: "Unknown error"}",
                bitmap = null,
                isFromUser = false
            )
        }
    }

    override suspend fun getResponseByPromptAndBitmap(prompt: String, bitmap: Bitmap): Chat {
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
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }catch (e:Exception) {
            return Chat(
                prompt = "Model error: ${e.message ?: "Unknown error"}",
                bitmap = null,
                isFromUser = false
            )
        }
    }

}




/*
object ChatData {



    suspend fun getResponseByPrompt( prompt : String) : Chat {

        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = api_key
        )

        try {
            val response  = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }catch (e : ResponseStoppedException) {
            return Chat(
                prompt = e.message ?: " error",
                bitmap = null,
                isFromUser = false
            )
        }
    }
    suspend fun getResponseByPromptAndBitmap( prompt : String , bitmap: Bitmap) :Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision",
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
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }catch (e:ResponseStoppedException) {
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }
    }
}
*/
