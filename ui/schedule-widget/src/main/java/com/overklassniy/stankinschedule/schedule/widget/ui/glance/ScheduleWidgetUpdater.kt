package com.overklassniy.stankinschedule.schedule.widget.ui.glance

import android.content.Context
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
}