package com.overklassniy.stankinschedule.news.widget.ui.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Планировщик периодического обновления виджета новостей через WorkManager.
 *
 * Запускает [NewsWidgetUpdateWorker] с интервалом 6 часов.
 * Использует [ExistingPeriodicWorkPolicy.KEEP], чтобы не дублировать уже запланированную работу.
 */
object NewsWidgetWorkerScheduler {

    /** Уникальное имя периодической работы. */
    private const val WORK_NAME = "news_widget_update"

    /** Интервал обновления в часах. */
    private const val UPDATE_INTERVAL_HOURS = 6L

    /**
     * Планирует периодическое обновление виджета новостей.
     *
     * @param context Контекст приложения.
     */
    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<NewsWidgetUpdateWorker>(
            UPDATE_INTERVAL_HOURS,
            TimeUnit.HOURS,
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}