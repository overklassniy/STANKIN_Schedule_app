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

object NewsNavEntry : BottomNavEntry(
    route = "news",
    nameRes = R.string.nav_news,
    iconRes = R.drawable.nav_news,
    hierarchy = listOf("news")
)

/**
 * Настраивает навигацию для модуля новостей.
 *
 * @param navController Контроллер навигации (не используется, но требуется для единообразия)
 */
@Suppress("UNUSED_PARAMETER")
fun NavGraphBuilder.news(navController: NavController) {
    composable(route = NewsNavEntry.route) {
        val context = LocalContext.current

        NewsReviewScreen(
            viewModel = hiltViewModel(),
            navigateToViewer = { newsTitle, newsId ->
                context.startActivity(NewsViewerActivity.createIntent(context, newsTitle, newsId))
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}