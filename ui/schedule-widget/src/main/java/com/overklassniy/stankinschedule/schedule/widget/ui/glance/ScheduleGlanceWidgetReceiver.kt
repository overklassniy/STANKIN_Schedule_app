package com.overklassniy.stankinschedule.schedule.widget.ui.glance

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver для Glance-виджета расписания.
 *
 * Связывает систему AppWidget с [ScheduleGlanceWidget].
 * При удалении виджета очищает его состояние из Glance Preferences.
 * При смене системной темы (тёмная/светлая) инициирует обновление виджета,
 * чтобы переоценить isSystemDarkTheme и применить корректные цвета.
 */
class ScheduleGlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = ScheduleGlanceWidget()

    /**
     * Обрабатывает входящие broadcast-сообщения.
     *
     * При получении ACTION_APPWIDGET_UPDATE_OPTIONS (например, при смене
     * системной темы) запускает обновление всех экземпляров виджета,
     * чтобы provideGlance выполнился заново с актуальным состоянием темы.
     *
     * @param context Контекст приложения.
     * @param intent Входящий broadcast Intent.
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED) {
            CoroutineScope(Dispatchers.IO).launch {
                ScheduleGlanceWidget().updateAll(context)
            }
        }
    }

    /**
     * Вызывается при удалении экземпляров виджета.
     * Очищает сохранённое состояние для каждого удалённого виджета.
     *
     * @param context Контекст приложения.
     * @param appWidgetIds Массив идентификаторов удалённых виджетов.
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        // Glance автоматически очищает состояние при удалении виджета,
        // поэтому дополнительных действий не требуется.
    }
}