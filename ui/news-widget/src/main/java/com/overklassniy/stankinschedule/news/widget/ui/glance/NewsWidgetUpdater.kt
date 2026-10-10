package com.overklassniy.stankinschedule.news.widget.ui.glance

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Утилита для обновления всех экземпляров виджета новостей.
 *
 * Вызывается из других модулей (например, из ui/news-review после обновления новостей)
 * для принудительной перерисовки виджетов.
 */
object NewsWidgetUpdater {

    /**
     * Асинхронно обновляет все экземпляры виджета новостей.
     *
     * @param context Контекст приложения.
     */
    fun updateAll(context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                NewsGlanceWidget().updateAll(context)
            } catch (e: Exception) {
                android.util.Log.e("NewsWidgetUpdater", "updateAll failed", e)
            }
        }
    }

    /**
     * Обновляет флаг isDarkTheme в Glance state всех виджетов новостей и перерисовывает их.
     *
     * Запись в state триггерит рекомпозицию через currentState, даже если
     * provideGlance ещё активен (updateAll не перезапускает его).
     *
     * @param context Контекст приложения.
     * @param isDark Признак тёмной системной темы.
     */
    fun updateThemeState(context: Context, isDark: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val manager = GlanceAppWidgetManager(context)
                val ids = manager.getGlanceIds(NewsGlanceWidget::class.java)
                for (id in ids) {
                    updateAppWidgetState(context, id) { prefs ->
                        prefs[NewsWidgetStateKeys.isDarkTheme] = isDark
                    }
                }
                NewsGlanceWidget().updateAll(context)
            } catch (e: Exception) {
                android.util.Log.e("NewsWidgetUpdater", "updateThemeState failed", e)
            }
        }
    }
}
