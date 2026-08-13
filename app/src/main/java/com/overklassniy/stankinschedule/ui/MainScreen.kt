package com.overklassniy.stankinschedule.ui

import android.content.Intent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.overklassniy.stankinschedule.navigation.HomeNavEntry
import com.overklassniy.stankinschedule.navigation.JournalNavEntry
import com.overklassniy.stankinschedule.navigation.ScheduleNavEntry
import com.overklassniy.stankinschedule.navigation.homePage
import com.overklassniy.stankinschedule.navigation.moduleJournal
import com.overklassniy.stankinschedule.navigation.schedule
import kotlinx.coroutines.launch

/**
 * Основной экран приложения, содержащий структуру навигации и общие элементы UI.
 *
 * Этот Composable настраивает [Scaffold] с нижней панелью навигации [AppNavigationBar]
 * и хостом навигации [NavHost], который управляет переключением между главными экранами:
 * Главная, Расписание, Журнал.
 *
 * Также здесь инициализируется состояние для отображения всплывающих уведомлений (Snackbar).
 *
 * @param intent Входящий Intent для обработки deep-link навигации, или null.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    intent: Intent? = null,
    onIntentHandled: () -> Unit = {}
) {
    // Состояние для управления отображением Snackbar (всплывающих сообщений)
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Функция-обертка для показа Snackbar в корутине
    val showSnackBarState: (message: String) -> Unit = { message ->
        scope.launch { snackBarHostState.showSnackbar(message) }
    }

    // Контроллер навигации для управления переходами между экранами
    val navController = rememberNavController()
    // Текущая запись в стеке навигации, используется для определения активного экрана в BottomBar
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Обработка входящего Intent для deep-link навигации
    LaunchedEffect(intent) {
        intent?.data?.let { uri ->
            // Парсим URI: app://stankinschedule.com/schedule/viewer/{scheduleId}?date={date}
            val segments = uri.pathSegments
            if (segments.size >= 3 && segments[0] == "schedule" && segments[1] == "viewer") {
                val scheduleId = segments[2].toLongOrNull()
                val date = uri.getQueryParameter("date")
                if (scheduleId != null) {
                    val route = if (date != null) {
                        "schedule/$scheduleId?date=$date"
                    } else {
                        "schedule/$scheduleId"
                    }
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }
            }
            // Очищаем Intent после обработки, чтобы избежать повторной навигации
            onIntentHandled()
        }
    }

    // Основной каркас экрана с нижней панелью и местом для контента
    Scaffold(
        bottomBar = {
            AppNavigationBar(
                navBackStackEntry = navBackStackEntry,
                navController = navController,
                screens = listOf(
                    HomeNavEntry,
                    ScheduleNavEntry,
                    JournalNavEntry
                )
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .exclude(WindowInsets.statusBars)
    ) { innerPadding ->
        // Граф навигации, определяющий доступные экраны и маршруты
        NavHost(
            navController = navController,
            startDestination = HomeNavEntry.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Экран "Главная"
            homePage(navController)
            // Раздел "Расписание"
            schedule(navController, showSnackBarState)
            // Раздел "Модульный журнал"
            moduleJournal(navController)
        }
    }
}