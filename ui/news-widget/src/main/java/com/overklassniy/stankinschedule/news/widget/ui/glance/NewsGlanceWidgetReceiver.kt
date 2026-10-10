package com.overklassniy.stankinschedule.news.widget.ui.glance

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver для Glance-виджета новостей.
 *
 * Обрабатывает системные интенты жизненного цикла виджета:
 * - Удаление виджета — очищает сохранённое состояние.
 * - Обновление виджета — перерисовывает содержимое.
 */
class NewsGlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = NewsGlanceWidget()

    /**
     * Вызывается при удалении виджета.
     *
     * Очищает сохранённое состояние виджета из Glance Preferences.
     * Запускается в фоновом coroutine, так как [updateAppWidgetState] — suspend-функция.
     *
     * @param context Контекст приложения.
     * @param appWidgetIds Массив идентификаторов удалённых виджетов.
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        CoroutineScope(Dispatchers.Default).launch {
            val manager = GlanceAppWidgetManager(context)
            for (id in appWidgetIds) {
                try {
                    val glanceId = manager.getGlanceIdBy(id)
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                        val mutable = prefs.toMutablePreferences()
                        mutable.remove(NewsWidgetStateKeys.subdivisions)
                        mutable.remove(NewsWidgetStateKeys.themePresetTag)
                        mutable.remove(NewsWidgetStateKeys.keyPrimary)
                        mutable.remove(NewsWidgetStateKeys.keySecondary)
                        mutable.remove(NewsWidgetStateKeys.keyTertiary)
                        mutable.remove(NewsWidgetStateKeys.keyBackground)
                        mutable.remove(NewsWidgetStateKeys.keySurface)
                        mutable.remove(NewsWidgetStateKeys.keyError)
                        mutable.remove(NewsWidgetStateKeys.textColor)
                        mutable.remove(NewsWidgetStateKeys.announcementsColor)
                        mutable
                    }
                } catch (_: Exception) {
                    // Игнорируем ошибки при очистке состояния
                }
            }
        }
    }
}
