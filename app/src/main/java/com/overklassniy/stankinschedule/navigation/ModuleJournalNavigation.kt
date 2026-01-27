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


/**
 * Запись навигации для экрана входа в модульный журнал.
 *
 * Является обычным пунктом назначения ([DestinationNavEntry]) без отображения в нижнем меню.
 */
object JournalLoginNavEntry : DestinationNavEntry(
    route = "journal/login"
)

/**
 * Запись навигации для основного экрана модульного журнала.
 *
 * Отображается в нижнем меню навигации.
 * В иерархию включен экран входа ([JournalLoginNavEntry]), чтобы при нахождении на нем
 * иконка журнала в меню оставалась активной.
 */
object JournalNavEntry : BottomNavEntry(
    route = "journal",
    nameRes = R.string.nav_journal,
    iconRes = R.drawable.nav_journal
)

/**
 * Настраивает граф навигации для раздела "Модульный журнал".
 *
 * Добавляет в граф два экрана:
 * 1. Экран входа в журнал ([JournalLoginNavEntry]).
 * 2. Основной экран просмотра журнала ([JournalNavEntry]).
 *
 * @receiver [NavGraphBuilder], в который добавляются маршруты журнала.
 * @param navController Контроллер навигации для управления переходами между экранами.
 */
fun NavGraphBuilder.moduleJournal(navController: NavController) {
    // Экран авторизации в журнале
    composable(route = JournalLoginNavEntry.route) {
        JournalLoginScreen(
            viewModel = hiltViewModel(),
            // После успешного входа переходим к просмотру журнала
            navigateToJournal = {
                navController.navigate(JournalNavEntry.route) {
                    // Очищаем стек навигации, чтобы нельзя было вернуться на экран входа кнопкой "Назад"
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    // Основной экран просмотра оценок и рейтинга
    composable(JournalNavEntry.route) {
        val context = LocalContext.current

        JournalScreen(
            viewModel = hiltViewModel(),
            // Если пользователь не авторизован или вышел, переходим на экран входа
            navigateToLogging = {
                navController.navigate(JournalLoginNavEntry.route)
            },
            // Открытие Activity для прогнозирования рейтинга (калькулятор баллов)
            navigateToPredict = {
                context.startActivity(
                    Intent(context, PredictActivity::class.java)
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}