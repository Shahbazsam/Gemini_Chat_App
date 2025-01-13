package com.example.geminichatapp.data.model

import androidx.annotation.StringRes
import com.example.geminichatapp.R

enum class NavigationRoutes (@StringRes val title: Int) {
    Splash(title = R.string.ask_genie),
    SignIn(title = R.string.signin),
    Chat(title = R.string.app_name),
    History(title = R.string.history),
    HistoryChat(title = R.string.historychat)
}