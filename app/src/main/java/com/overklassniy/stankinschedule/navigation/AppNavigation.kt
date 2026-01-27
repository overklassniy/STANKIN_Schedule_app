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
import com.overklassniy.stankinschedule.core.ui.utils.BrowserUtils
import com.overklassniy.stankinschedule.home.ui.HomeScreen
import com.overklassniy.stankinschedule.navigation.entry.BottomNavEntry
import com.overklassniy.stankinschedule.settings.ui.SettingsActivity

/**
 * Запись навигации для главного экрана приложения.
 *
 * Используется нижней панелью навигации для отображения пункта "Главная".
 */
object HomeNavEntry : BottomNavEntry(
    route = "home", nameRes = R.string.nav_home, iconRes = R.drawable.nav_home
)

/**
 * Базовый URL раздела новостей на официальном сайте МГТУ «СТАНКИН».
 *
 * Используется для открытия страницы со списком новостей
 * и для формирования ссылок на конкретные новости.
 */
private const val STANKIN_NEWS = "https://stankin.ru/news/"

/**
 * Добавляет в граф навигации главный экран приложения.
 *
 * Настраивает маршрут [HomeNavEntry.route] и прокидывает в [HomeScreen]
 * обработчики переходов к экрану расписания, новостей и настроек.
 *
 * @receiver [NavGraphBuilder], в который добавляется маршрут главного экрана.
 * @param navController Контроллер навигации, используемый для переходов
 * на другие экраны (расписание, журнал и т.д.).
 */
fun NavGraphBuilder.homePage(navController: NavController) {
    composable(route = HomeNavEntry.route) {

        // Текущий контекст приложения, используется для открытия экранов и браузера
        val context = LocalContext.current

        HomeScreen(
            // ViewModel главного экрана, получаемый через Hilt
            viewModel = hiltViewModel(),

            // Переход к экрану просмотра выбранного расписания
            navigateToSchedule = { scheduleId ->
                navController.navigate(
                    route = ScheduleViewerNavEntry.routeWithArgs(
                        scheduleId
                    )
                ) {
                    // Очищаем стек до стартового экрана, сохраняя состояние
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                }
            },

            // Открытие страницы со списком новостей в браузере
            navigateToNews = {
                BrowserUtils.openLink(context, STANKIN_NEWS)
            },

            // Открытие конкретной новости по URL, если он есть, иначе открываем список новостей
            navigateToNewsPost = { post ->
                val url = post.relativeUrl ?: STANKIN_NEWS
                BrowserUtils.openLink(context, url)
            },

            // Переход к экрану настроек приложения
            navigateToSettings = {
                context.startActivity(
                    Intent(context, SettingsActivity::class.java)
                )
            },

            // Заполняем весь доступный размер экрана
            modifier = Modifier.fillMaxSize()
        )
    }
}