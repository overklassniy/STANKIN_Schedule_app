package com.overklassniy.stankinschedule.schedule.widget.ui.base

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViewsService.RemoteViewsFactory
import kotlinx.coroutines.runBlocking

/**
 * Базовая фабрика RemoteViews, работающая с корутинами.
 *
 * Назначение: предоставляет шаблон для асинхронной загрузки данных
 * через suspend‑функцию onDataChanged и безопасного вызова её из onDataSetChanged.
 *
 * @property packageName Имя пакета приложения. Используется при создании RemoteViews.
 * @property appWidgetId Идентификатор виджета из Intent extras.
 *
 * Исключения: внутренняя отмена корутин может вызывать CancellationException,
 * наружу не пробрасывается.
 */
abstract class CoroutinesRemoteFactory(
    context: Context,
    intent: Intent
) : RemoteViewsFactory {

    /** Имя пакета приложения для конструирования RemoteViews. */
    val packageName: String = context.packageName

    /** Идентификатор виджета, извлекаемый из Intent. */
    val appWidgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
    )

    override fun onCreate() {}

    /**
     * Вызывается системой при изменении данных.
     *
     * Внутри выполняет suspend onDataChanged в блокирующем контексте runBlocking,
     * так как RemoteViewsFactory не поддерживает suspend напрямую.
     */
    override fun onDataSetChanged() {
        // Вызов suspend‑логики в синхронном контексте.
        runBlocking { onDataChanged() }
    }

    /**
     * Загружает и подготавливает данные для отображения.
     * Должна быть безопасной к повторным вызовам.
     */
    abstract suspend fun onDataChanged()

    override fun onDestroy() {}

    /** Количество типов представления элементов. По умолчанию один. */
    override fun getViewTypeCount(): Int = 1

    /** Используются стабильные идентификаторы элементов. */
    override fun hasStableIds(): Boolean = true

    /** Возвращает стабильный идентификатор элемента по позиции. */
    override fun getItemId(position: Int): Long = position.toLong()

}