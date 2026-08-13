package com.overklassniy.stankinschedule.settings.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.overklassniy.stankinschedule.core.domain.repository.ProductAnalytics
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Экран просмотра Markdown-документов.
 *
 * Отвечает за загрузку и отображение документов в WebView через Compose.
 */
@AndroidEntryPoint
class MarkdownViewerActivity : AppCompatActivity() {

    @Inject
    lateinit var productAnalytics: ProductAnalytics

    private val viewModel: MarkdownViewerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""
        val url = intent.getStringExtra(EXTRA_URL) ?: ""

        setContent {
            AppTheme {
                val state by viewModel.markdownContent.collectAsStateWithLifecycle()

                MarkdownViewerScreen(
                    title = title,
                    state = state,
                    onRetry = { viewModel.loadMarkdown(url) },
                    onBackPressed = { onBackPressedDispatcher.onBackPressed() }
                )
            }
        }

        viewModel.loadMarkdown(url)

        productAnalytics.logEvent(ProductAnalytics.SCREEN_ENTER, "MarkdownViewerActivity")
    }

    override fun onDestroy() {
        super.onDestroy()
        productAnalytics.logEvent(ProductAnalytics.SCREEN_LEAVE, "MarkdownViewerActivity")
    }

    companion object {
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_URL = "extra_url"

        /**
         * Создаёт Intent для запуска экрана просмотра Markdown.
         *
         * @param context Контекст.
         * @param title Заголовок документа.
         * @param url URL документа.
         * @return Intent для запуска [MarkdownViewerActivity].
         */
        fun createIntent(context: Context, title: String, url: String): Intent {
            return Intent(context, MarkdownViewerActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_URL, url)
            }
        }
    }
}