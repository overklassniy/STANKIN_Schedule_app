package com.overklassniy.stankinschedule.news.widget.ui.configure

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import com.overklassniy.stankinschedule.news.widget.domain.model.NewsWidgetData
import com.overklassniy.stankinschedule.news.widget.ui.components.NewsWidgetConfigureScreen
import com.overklassniy.stankinschedule.news.widget.ui.glance.NewsGlanceWidget
import com.overklassniy.stankinschedule.news.widget.ui.glance.NewsWidgetStateKeys
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity конфигурации виджета новостей.
 *
 * Отображает Compose-экран конфигурации, сохраняет настройки в Glance state
 * и возвращает результат системе AppWidgetManager.
 */
@AndroidEntryPoint
class NewsWidgetConfigureActivity : AppCompatActivity() {

    private val viewModel: NewsWidgetConfigureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_CANCELED)

        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        )

        setContent {
            AppTheme {
                NewsWidgetConfigureScreen(
                    appWidgetId = appWidgetId,
                    viewModel = viewModel,
                    onBackPressed = { onBackPressedDispatcher.onBackPressed() },
                    onNewsWidgetChanged = ::onNewsWidgetChanged,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    /**
     * Обработка подтверждения настроек виджета.
     *
     * Сохраняет данные в Glance state и запускает обновление виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @param data Настройки виджета.
     */
    private fun onNewsWidgetChanged(appWidgetId: Int, data: NewsWidgetData) {
        Log.d(TAG, "onNewsWidgetChanged: appWidgetId=$appWidgetId, data=$data")
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val manager = GlanceAppWidgetManager(this@NewsWidgetConfigureActivity)
                val glanceId = manager.getGlanceIdBy(appWidgetId)

                updateAppWidgetState(this@NewsWidgetConfigureActivity, glanceId) { prefs ->
                    NewsWidgetStateKeys.writeToPrefs(prefs, data)
                }

                NewsGlanceWidget().update(this@NewsWidgetConfigureActivity, glanceId)

                withContext(Dispatchers.Main) {
                    setResult(
                        RESULT_OK,
                        Intent().apply { putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) }
                    )
                    finish()
                }
            } catch (e: Exception) {
                Log.e(TAG, "onNewsWidgetChanged: error", e)
            }
        }
    }

    private companion object {
        private const val TAG = "NewsWidgetConfigure"
    }
}
