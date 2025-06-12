package com.example.bugjournal

import android.net.Uri
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.bugjournal.ui.theme.AddBugScreen
import com.example.bugjournal.ui.theme.BugDetailScreen
import com.example.bugjournal.ui.theme.HomeScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(auth.currentUser) }

    // 🔄 Listen to auth changes
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)

        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

    // 🔁 Auto navigate on auth change
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate("auth") {
                popUpTo("home") { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // 🧭 Main navigation


            NavHost(
                navController = navController,
                startDestination = if (currentUser == null) "auth" else "home"
            ) {
                composable("auth") { AuthScreen(navController) }
                composable("home") { HomeScreen(navController) }
                composable("addBug") { AddBugScreen(navController) }

                // 🛠️ Handle bug detail navigation with encoded title
                composable("bugDetail/{bugTitle}") { backStackEntry ->
                    val encodedTitle = backStackEntry.arguments?.getString("bugTitle")
                    val bugTitle = encodedTitle?.let { Uri.decode(it) }

                    if (!bugTitle.isNullOrBlank()) {
                        BugDetailScreen(bugTitle = bugTitle, navController = navController)
                    }
                }
            }


}
