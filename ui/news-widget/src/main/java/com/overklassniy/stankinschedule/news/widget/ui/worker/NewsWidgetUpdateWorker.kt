package com.overklassniy.stankinschedule.news.widget.ui.worker

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision
import com.overklassniy.stankinschedule.news.core.domain.usecase.NewsReviewUseCase
import com.overklassniy.stankinschedule.news.widget.ui.glance.NewsGlanceWidget
import com.overklassniy.stankinschedule.news.widget.ui.glance.NewsWidgetStateKeys
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker для периодического обновления новостей в виджете.
 *
 * Алгоритм:
 * 1. Получает список всех экземпляров виджета новостей.
 * 2. Для каждого виджета читает конфигурацию из Glance state.
 * 3. Для каждой выбранной категории вызывает принудительное обновление новостей через [NewsReviewUseCase.refreshNews].
 * 4. Запускает перерисовку всех виджетов.
 */
@HiltWorker
class NewsWidgetUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val newsReviewUseCase: NewsReviewUseCase,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "doWork: start")

            val context = applicationContext
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(NewsGlanceWidget::class.java)

            if (glanceIds.isEmpty()) {
                Log.d(TAG, "doWork: no widget instances found")
                return Result.success()
            }

            // Сбор всех уникальных категорий из всех виджетов
            val allSubdivisions = mutableSetOf<NewsSubdivision>()

            for (glanceId in glanceIds) {
                val prefs = getAppWidgetState<Preferences>(
                    context,
                    PreferencesGlanceStateDefinition,
                    glanceId,
                )
                val data = NewsWidgetStateKeys.toData(prefs)
                if (data != null) {
                    allSubdivisions.addAll(data.selectedSubdivisions)
                }
            }

            // Принудительное обновление новостей для каждой категории
            for (subdivision in allSubdivisions) {
                try {
                    newsReviewUseCase.refreshNews(subdivision.id, force = true)
                    Log.d(TAG, "doWork: refreshed subdivision=${subdivision.name}")
                } catch (e: Exception) {
                    Log.e(TAG, "doWork: failed to refresh subdivision=${subdivision.name}", e)
                }
            }

            // Перерисовка всех виджетов
            NewsGlanceWidget().updateAll(context)
            Log.d(TAG, "doWork: done, refreshed ${allSubdivisions.size} subdivisions")

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: error", e)
            Result.retry()
        }
    }

    private companion object {
        private const val TAG = "NewsWidgetWorker"
    }
}
