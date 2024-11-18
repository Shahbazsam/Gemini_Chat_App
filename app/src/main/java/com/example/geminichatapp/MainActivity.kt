package com.example.geminichatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.geminichatapp.ui.presentation.ChatBotAppBar
import com.example.geminichatapp.ui.theme.GeminiChatAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeminiChatAppTheme {
                ChatBotAppBar()
            }
        }
    }
}

