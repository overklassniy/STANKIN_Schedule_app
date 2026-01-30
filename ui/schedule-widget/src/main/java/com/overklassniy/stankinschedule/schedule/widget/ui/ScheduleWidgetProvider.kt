package com.overklassniy.stankinschedule.schedule.widget.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import com.overklassniy.stankinschedule.schedule.widget.ui.utils.ScheduleDeepLink

/**
 * Провайдер виджета расписания.
 *
 * Обрабатывает широковещательные события и обновления виджета.
 */
class ScheduleWidgetProvider : AppWidgetProvider() {

    /**
     * Обработка входящих интентов. Логирует deep‑link действия.
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action
        if (!action.isNullOrEmpty() && action == ScheduleDeepLink.SCHEDULE_VIEWER_ACTION) {
            Log.d("ScheduleWidgetProvider", "onReceive: ${intent.data}")
        }

    }

    /**
     * Обновление экземпляров виджета.
     * Загружает сохраненные данные и инициирует перерисовку.
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val preference = ScheduleWidget.widgetPreference(context)

        try {
            appWidgetIds.forEach { appWidgetId ->
                val data = preference.loadData(appWidgetId)
                ScheduleWidget.onUpdateWidget(context, appWidgetManager, appWidgetId, data, true)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * Удаление виджетов: очищает сохраненные данные из репозитория.
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val preference = ScheduleWidget.widgetPreference(context)
        appWidgetIds.forEach { appWidgetId -> preference.deleteData(appWidgetId) }
    }
}