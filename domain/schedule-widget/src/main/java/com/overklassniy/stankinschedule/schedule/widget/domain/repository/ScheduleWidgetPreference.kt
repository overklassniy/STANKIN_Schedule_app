package com.overklassniy.stankinschedule.schedule.widget.domain.repository

import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData

/**
 * Хранилище настроек/данных виджета расписания.
 */
interface ScheduleWidgetPreference {

    /**
     * Загружает сохранённые данные для виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return [ScheduleWidgetData] или null, если данных нет.
     */
    fun loadData(appWidgetId: Int): ScheduleWidgetData?

    /**
     * Сохраняет данные конфигурации виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @param data Данные виджета для сохранения.
     */
    fun saveData(appWidgetId: Int, data: ScheduleWidgetData)

    /**
     * Удаляет сохранённые данные виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     */
    fun deleteData(appWidgetId: Int)
}