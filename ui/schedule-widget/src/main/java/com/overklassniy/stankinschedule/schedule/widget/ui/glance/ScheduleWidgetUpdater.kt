package com.overklassniy.stankinschedule.schedule.widget.ui.glance

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Утилитный объект для обновления Glance-виджетов расписания из других модулей.
 *
 * Инкапсулирует вызовы Glance API, чтобы модули без зависимости от Glance
 * могли обновлять виджеты.
 */
object ScheduleWidgetUpdater {

    /**
     * Обновляет все экземпляры виджета расписания.
     *
     * @param context Контекст приложения.
     */
    fun updateAll(context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            ScheduleGlanceWidget().updateAll(context)
        }
    }

    /**
     * Обновляет флаг isDarkTheme в Glance state всех виджетов расписания и перерисовывает их.
     *
     * Запись в state триггерит рекомпозицию через currentState, даже если
     * provideGlance ещё активен (updateAll не перезапускает его).
     *
     * @param context Контекст приложения.
     * @param isDark Признак тёмной системной темы.
     */
    fun updateThemeState(context: Context, isDark: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(ScheduleGlanceWidget::class.java)
            for (id in ids) {
                updateAppWidgetState(context, id) { prefs ->
                    prefs[WidgetStateKeys.isDarkTheme] = isDark
                }
            }
            ScheduleGlanceWidget().updateAll(context)
        }
    }
}