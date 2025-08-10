package com.hul0.mindflow.navigation

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hul0.mindflow.ui.screens.*
import com.hul0.mindflow.ui.viewmodel.ChatViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier.padding(paddingValues)
    ) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Quotes.route) { QuotesScreen() }
        composable(Screen.Meditation.route) { MeditationScreen() }
        composable(Screen.MoodTracker.route) { MoodTrackerScreen() }
        composable(Screen.Todo.route) { TodoScreen() }
        composable(Screen.SleepTracker.route) { SleepTrackerScreen() }
        composable(Screen.Journal.route) { JournalScreen() }
        composable(Screen.FunFacts.route) { FunFactsScreen() }
        composable(Screen.MentalHealthTips.route) { MentalHealthTipsScreen() }
        composable(Screen.BmiCalculator.route) { BmiCalculatorScreen() }
        composable(Screen.Breathwork.route) { BreathworkScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }

        // Corrected route for ChatScreen
        composable(Screen.Chat.route) {
            // 1. Get the application context, which is needed by the ViewModelFactory.
            val application = LocalContext.current.applicationContext as Application

            // 2. Create the ChatViewModel using the viewModel() composable function.
            //    This correctly scopes the ViewModel to the navigation graph.
            val chatViewModel: ChatViewModel = viewModel(
                factory = ViewModelFactory(application)
            )

            // 3. Pass the ViewModel instance to your ChatScreen.
            //    Note: This assumes your ChatScreen takes a ChatViewModel as a parameter.
            ChatScreen(viewModel = chatViewModel)
        }
    }
}
