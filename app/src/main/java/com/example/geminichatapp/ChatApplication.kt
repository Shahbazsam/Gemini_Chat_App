package com.example.geminichatapp

import android.app.Application
import com.example.geminichatapp.data.container.AppContainer
import com.example.geminichatapp.data.container.AppContainerImpl

class ChatApplication : Application()  {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}