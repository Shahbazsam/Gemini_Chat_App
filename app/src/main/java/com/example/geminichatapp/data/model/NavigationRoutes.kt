package com.example.geminichatapp.data.model

import androidx.annotation.StringRes
import com.example.geminichatapp.R

enum class NavigationRoutes (@StringRes val title: Int) {
    Splash(title = R.string.ask_genie),
    Chat(title = R.string.app_name)
}