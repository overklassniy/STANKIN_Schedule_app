package com.overklassniy.stankinschedule.navigation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.compose.material.navigation.bottomSheet
import com.overklassniy.stankinschedule.R
import com.overklassniy.stankinschedule.navigation.entry.BottomNavEntry

import com.overklassniy.stankinschedule.navigation.entry.DestinationNavEntry
import com.overklassniy.stankinschedule.schedule.creator.ui.ScheduleCreatorSheet
import com.overklassniy.stankinschedule.schedule.editor.ui.PairEditorActivity
import com.overklassniy.stankinschedule.schedule.list.ui.ScheduleScreen
import com.overklassniy.stankinschedule.schedule.parser.ui.ScheduleParserActivity
import com.overklassniy.stankinschedule.schedule.repository.ui.ScheduleRepositoryActivity
import com.overklassniy.stankinschedule.schedule.table.ui.ScheduleTableViewActivity
import com.overklassniy.stankinschedule.schedule.viewer.ui.ScheduleViewerScreen
import com.overklassniy.stankinschedule.schedule.widget.ui.utils.ScheduleDeepLink

object ScheduleNavEntry : BottomNavEntry(
    route = "schedule",
    nameRes = R.string.nav_schedule,
    iconRes = R.drawable.nav_schedule,
    hierarchy = listOf("schedule", ScheduleViewerNavEntry.route, ScheduleCreatorNavEntry.route)
)

object ScheduleViewerNavEntry : DestinationNavEntry(
    route = "schedule/{scheduleId}?date={startDate}",
    arguments = listOf(
        navArgument(name = "scheduleId") { type = NavType.LongType },
        navArgument(name = "startDate") { type = NavType.StringType; nullable = true }
    )
) {
    fun routeWithArgs(id: Long): String = "schedule/$id"
    fun parseScheduleId(entry: NavBackStackEntry) = entry.arguments?.getLong("scheduleId") ?: -1

    fun parseStartDate(entry: NavBackStackEntry) = entry.arguments?.getString("startDate")
}

object ScheduleCreatorNavEntry : DestinationNavEntry(
    route = "schedule/creator",
)

@OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
fun NavGraphBuilder.schedule(
    navController: NavController,
    showSnackBarState: (message: String) -> Unit
) {
    // Мои расписания
    composable(route = ScheduleNavEntry.route) {
        ScheduleScreen(
            onScheduleCreate = {
                navController.navigate(route = ScheduleCreatorNavEntry.route)
            },
            onScheduleClicked = { id ->
                navController.navigate(route = ScheduleViewerNavEntry.routeWithArgs(id))
            },
            viewModel = hiltViewModel(),
            modifier = Modifier.fillMaxSize()
        )
    }
    // Просмотр расписания
    composable(
        route = ScheduleViewerNavEntry.route,
        arguments = ScheduleViewerNavEntry.arguments,
        deepLinks = listOf(navDeepLink { uriPattern = ScheduleDeepLink.DEEP_LINK })
    ) { backStackEntry ->
        val context = LocalContext.current

        ScheduleViewerScreen(
            scheduleId = ScheduleViewerNavEntry.parseScheduleId(backStackEntry),
            startDate = ScheduleViewerNavEntry.parseStartDate(backStackEntry),
            scheduleName = null,
            viewModel = hiltViewModel(),
            onBackPressed = { navController.navigateUp() },
            onEditorClicked = { scheduleId, pairId ->
                val intent = PairEditorActivity.createIntent(
                    context, scheduleId, pairId
                )
                context.startActivity(intent)
            },
            onTableViewClicked = { scheduleId ->
                val intent = ScheduleTableViewActivity.createIntent(context, scheduleId)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
    // Создание расписания
    bottomSheet(route = ScheduleCreatorNavEntry.route) {
        val context = LocalContext.current

        ScheduleCreatorSheet(
            onNavigateBack = { navController.navigateUp() },
            onRepositoryClicked = {
                context.startActivity(Intent(context, ScheduleRepositoryActivity::class.java))
                navController.navigateUp()
            },
            onImportClicked = {
                context.startActivity(Intent(context, ScheduleParserActivity::class.java))
                navController.navigateUp()
            },
            onShowSnackBar = showSnackBarState,
            viewModel = hiltViewModel(),
            modifier = Modifier.navigationBarsPadding()
        )
    }
}