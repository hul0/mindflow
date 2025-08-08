package com.hul0.mindflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.hul0.mindflow.navigation.AppNavigation
import com.hul0.mindflow.navigation.Screen
import com.hul0.mindflow.ui.components.BottomNavigationBar
import com.hul0.mindflow.ui.theme.MindFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is the key to handling display cutouts and system bars correctly.
        // It allows the app to draw behind the system bars for a modern, immersive look.
        enableEdgeToEdge()

        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(this) {}

        setContent {
            MindFlowTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define the list of screens where the BottomNavigationBar should be visible.
    val bottomNavRoutes = setOf(
        Screen.Home.route,
        Screen.Quotes.route,
        Screen.MoodTracker.route,
        Screen.Meditation.route,
        Screen.Profile.route
    )

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        // We create a new PaddingValues object that only uses the top padding
        // from the Scaffold. This allows the screen content to extend to the
        // bottom of the screen, drawing behind the navigation bar.
        val navHostPadding = PaddingValues(top = innerPadding.calculateTopPadding())

        AppNavigation(
            navController = navController,
            paddingValues = navHostPadding
        )
    }
}
