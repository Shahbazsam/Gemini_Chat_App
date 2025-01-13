package com.example.geminichatapp

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.geminichatapp.data.model.NavigationRoutes
import com.example.geminichatapp.ui.presentation.screens.ChatScreen
import com.example.geminichatapp.ui.presentation.screens.HistoryChatList
import com.example.geminichatapp.ui.presentation.screens.HistoryScreen
import com.example.geminichatapp.ui.presentation.screens.HistoryViewModel
import com.example.geminichatapp.ui.presentation.screens.SplashScreen
import com.example.geminichatapp.ui.presentation.sign_in.GoogleAuthUiClient
import com.example.geminichatapp.ui.presentation.sign_in.SignInScreen
import com.example.geminichatapp.ui.presentation.sign_in.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

@Composable
fun AppManager(
    navController: NavHostController = rememberNavController()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleScope = remember{lifecycleOwner.lifecycleScope}
    val context = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }
    val historyViewModel : HistoryViewModel = viewModel(factory = HistoryViewModel.factory)

    Scaffold(
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.Splash.name
        ) {
            composable(route = NavigationRoutes.Splash.name) {
                SplashScreen(onNextScreen = {
                    navController.navigate(NavigationRoutes.SignIn.name) {
                        popUpTo(NavigationRoutes.Splash.name) { inclusive = true }
                    }
                })
            }
            composable(route = NavigationRoutes.SignIn.name) {
                val viewModel = viewModel<SignInViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(key1 = Unit) {
                    if (googleAuthUiClient.getSignedInUser() != null){
                        navController.navigate(NavigationRoutes.Chat.name)
                    }
                }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult() ,
                    onResult = { result ->
                        if (result.resultCode == RESULT_OK) {
                            lifecycleScope.launch {
                                val signInResult = googleAuthUiClient.getSignInResultWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                viewModel.onSignInResult(signInResult)
                            }
                        }
                    }
                )
                LaunchedEffect(key1 = state.isSignInSuccessful) {
                    if(state.isSignInSuccessful) {
                        Toast.makeText(
                            context,
                            "Sign-In Successful",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigate(NavigationRoutes.Chat.name)
                        viewModel.resetState()
                    }
                }
                SignInScreen(
                    state = state,
                    onSignInClick = {
                        lifecycleScope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )
                        }
                    }
                )
            }

            composable(route = NavigationRoutes.Chat.name) {
                ChatScreen(
                    userData = googleAuthUiClient.getSignedInUser(),
                    paddingValues = innerPadding,
                    onNavigate = {
                        navController.navigate(NavigationRoutes.History.name)
                    }

                )
            }
            composable(route = NavigationRoutes.History.name) {
                HistoryScreen(
                    historyViewModel = historyViewModel,
                    onSignOut = {
                        lifecycleScope.launch {
                            googleAuthUiClient.signOut()
                            Toast.makeText(
                                context,
                                "signed Out Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.navigate(NavigationRoutes.SignIn.name) {
                                popUpTo(NavigationRoutes.SignIn.name) { inclusive = true}
                            }
                        }
                    },
                    userData = googleAuthUiClient.getSignedInUser(),
                    onNavigate = {
                        navController.navigate(NavigationRoutes.HistoryChat.name)
                    },
                    onBackPress = {
                        navController.navigate(NavigationRoutes.Chat.name)
                    }
                )
            }
            composable(route = NavigationRoutes.HistoryChat.name) {
               HistoryChatList(
                   historyViewModel = historyViewModel,
                   paddingValues = innerPadding,
                   onNavigate = {
                       navController.navigate(NavigationRoutes.History.name)
                   },
                   onBackPress = {
                       navController.navigate(NavigationRoutes.Chat.name)
                   }
               )
            }
        }
    }
}

