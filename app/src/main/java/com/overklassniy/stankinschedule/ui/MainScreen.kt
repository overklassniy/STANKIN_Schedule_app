package com.overklassniy.stankinschedule.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
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