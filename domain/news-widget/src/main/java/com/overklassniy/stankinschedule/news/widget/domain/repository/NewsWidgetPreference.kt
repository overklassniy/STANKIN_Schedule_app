package com.overklassniy.stankinschedule.news.widget.domain.repository

import com.overklassniy.stankinschedule.news.widget.domain.model.NewsWidgetData

/**
 * Хранилище настроек виджета новостей (legacy-интерфейс для миграции).
 */
interface NewsWidgetPreference {

    /**
     * Загружает сохранённые данные для виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return [NewsWidgetData] или null, если данных нет.
     */
    fun loadData(appWidgetId: Int): NewsWidgetData?

    /**
     * Сохраняет данные конфигурации виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @param data Данные виджета для сохранения.
     */
    fun saveData(appWidgetId: Int, data: NewsWidgetData)

    /**
     * Удаляет сохранённые данные виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     */
    fun deleteData(appWidgetId: Int)
}