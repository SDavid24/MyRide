package com.newagedavid.myride.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.newagedavid.myride.presentation.ui.history.RideHistoryScreen
import com.newagedavid.myride.presentation.ui.home.HomeScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Set status bar to light mode for black text/icons
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent, // Keep status bar transparent for edge-to-edge
            darkIcons = true // Use dark (black) icons/text
        )
    }

    Scaffold(
        modifier = Modifier.padding(bottom = 35.dp),

        bottomBar = {
            if (showBottomBar(currentRoute)) {
                BottomNavigationBar(navController)
            }
        },

        contentWindowInsets = WindowInsets(0, 0, 0, 0), // remove default inset padding

    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding) // Apply padding to NavHost
        ) {
            composable("home") {
                HomeScreen(
                    navController = navController,
                )
            }
            composable("history") {
                RideHistoryScreen(
                    navController = navController,
                )
            }
        }
    }
}


