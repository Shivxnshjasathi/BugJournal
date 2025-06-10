package com.example.bugjournal

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.bugjournal.ui.theme.AddBugScreen
import com.example.bugjournal.ui.theme.HomeScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    var isUserChecked by remember { mutableStateOf(false) }

    // This will determine the actual start screen based on user's login status
    val startDestination = remember(isUserChecked) {
        if (auth.currentUser != null) "home" else "auth"
    }

    // Delay navigation until user check is done
    LaunchedEffect(Unit) {
        isUserChecked = true
    }

    if (isUserChecked) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable("auth") { AuthScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("addBug") { AddBugScreen(navController) }
        }
    }
}