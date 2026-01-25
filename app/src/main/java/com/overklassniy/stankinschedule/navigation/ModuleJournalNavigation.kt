package com.overklassniy.stankinschedule.navigation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.overklassniy.stankinschedule.R
import com.overklassniy.stankinschedule.journal.login.ui.JournalLoginScreen
import com.overklassniy.stankinschedule.journal.predict.ui.PredictActivity
import com.overklassniy.stankinschedule.journal.viewer.ui.JournalScreen
import com.overklassniy.stankinschedule.navigation.entry.BottomNavEntry
import com.overklassniy.stankinschedule.navigation.entry.DestinationNavEntry


object JournalLoginNavEntry : DestinationNavEntry(
    route = "journal/login"
)

object JournalNavEntry : BottomNavEntry(
    route = "journal",
    nameRes = R.string.nav_journal,
    iconRes = R.drawable.nav_journal,
    hierarchy = listOf("journal", JournalLoginNavEntry.route)
)

fun NavGraphBuilder.moduleJournal(navController: NavController) {
    // Авторизация
    composable(route = JournalLoginNavEntry.route) {
        JournalLoginScreen(
            viewModel = hiltViewModel(),
            navigateToJournal = {
                navController.navigate(JournalNavEntry.route) {
                    // Убрать экран с логином
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
    // Просмотр журнала
    composable(JournalNavEntry.route) {
        val context = LocalContext.current

        JournalScreen(
            viewModel = hiltViewModel(),
            navigateToLogging = {
                navController.navigate(JournalLoginNavEntry.route)
            },
            navigateToPredict = {
                context.startActivity(
                    Intent(context, PredictActivity::class.java)
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}