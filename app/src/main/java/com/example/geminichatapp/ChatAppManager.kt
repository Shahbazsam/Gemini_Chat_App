package com.example.geminichatapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.geminichatapp.data.model.NavigationRoutes
import com.example.geminichatapp.ui.presentation.screens.ChatScreen
import com.example.geminichatapp.ui.presentation.screens.SplashScreen
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotAppBar(
    currentScreen: NavigationRoutes,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(currentScreen.title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun AppManager(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = NavigationRoutes.valueOf(
        backStackEntry?.destination?.route ?: NavigationRoutes.Chat.name
    )

    Scaffold(
        topBar = {
            if (currentScreen != NavigationRoutes.Splash) {
                ChatBotAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.Splash.name
        ) {
            composable(route = NavigationRoutes.Splash.name) {
                SplashScreen(onNextScreen = {
                    navController.navigate(NavigationRoutes.Chat.name) {
                        popUpTo(NavigationRoutes.Splash.name) { inclusive = true }
                    }
                })
            }

            composable(route = NavigationRoutes.Chat.name) {
                ChatScreen(paddingValues = innerPadding)
            }
        }
    }
}

