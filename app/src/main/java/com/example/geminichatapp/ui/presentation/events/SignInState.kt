package com.example.geminichatapp.ui.presentation.events

data class SignInState(
    val isSignInSuccessful : Boolean = false,
    val signInError : String? = null
)
