package com.overklassniy.stankinschedule.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.overklassniy.stankinschedule.R
import com.overklassniy.stankinschedule.navigation.entry.BottomNavEntry
import com.overklassniy.stankinschedule.news.review.ui.NewsReviewScreen
import com.overklassniy.stankinschedule.news.viewer.ui.NewsViewerActivity

/**
 * Элемент навигации для экрана "Новости" в нижнем меню приложения.
 *
 * Наследуется от [BottomNavEntry] и определяет основные параметры навигации:
 * маршрут, ресурс строки названия, ресурс иконки и иерархию маршрутов.
 */
object NewsNavEntry : BottomNavEntry(
    route = "news",
    nameRes = R.string.nav_news,
    iconRes = R.drawable.nav_news
)

/**
 * Функция-расширение для [NavGraphBuilder], настраивающая граф навигации раздела новостей.
 *
 * Добавляет в граф навигации экран списка новостей ([NewsReviewScreen]).
 *
 * @receiver [NavGraphBuilder] Строитель графа навигации, к которому добавляется маршрут.
 * @param navController [NavController] Контроллер навигации.
 *                      В данной функции не используется напрямую (помечен @Suppress("UNUSED_PARAMETER")),
 *                      но оставлен для сохранения единой сигнатуры функций навигации.
 */
@Suppress("UNUSED_PARAMETER")
fun NavGraphBuilder.news(navController: NavController) {
    // Определяем composable экран для маршрута новостей
    composable(route = NewsNavEntry.route) {
        val context = LocalContext.current

        // Экран отображения списка новостей
        NewsReviewScreen(
            // Получаем экземпляр ViewModel, внедренный через Hilt
            viewModel = hiltViewModel(),
            // Callback для перехода к просмотру конкретной новости
            navigateToViewer = { newsTitle, newsId ->
                // Запускаем отдельную Activity для просмотра содержимого новости
                context.startActivity(NewsViewerActivity.createIntent(context, newsTitle, newsId))
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}