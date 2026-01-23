package com.overklassniy.stankinschedule.navigation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.overklassniy.stankinschedule.R
import com.overklassniy.stankinschedule.core.ui.utils.BrowserUtils
import com.overklassniy.stankinschedule.home.ui.HomeScreen
import com.overklassniy.stankinschedule.navigation.entry.BottomNavEntry
import com.overklassniy.stankinschedule.settings.ui.SettingsActivity

object HomeNavEntry : BottomNavEntry(
    route = "home",
    nameRes = R.string.nav_home,
    iconRes = R.drawable.nav_home
)

private const val STANKIN_NEWS = "https://stankin.ru/news/"

fun NavGraphBuilder.homePage(navController: NavController) {
    composable(route = HomeNavEntry.route) {

        val context = LocalContext.current

        HomeScreen(
            viewModel = hiltViewModel(),
            navigateToSchedule = { scheduleId ->
                navController.navigate(
                    route = ScheduleViewerNavEntry.routeWithArgs(
                        scheduleId
                    )
                ) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                }
            },
            navigateToNews = {
                BrowserUtils.openLink(context, STANKIN_NEWS)
            },
            navigateToNewsPost = { post ->
                val url = post.relativeUrl ?: STANKIN_NEWS
                BrowserUtils.openLink(context, url)
            },
            navigateToSettings = {
                context.startActivity(
                    Intent(context, SettingsActivity::class.java)
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}