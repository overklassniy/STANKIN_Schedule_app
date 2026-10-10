package com.overklassniy.stankinschedule.news.widget.ui.glance

import com.overklassniy.stankinschedule.core.domain.repository.CrashReporter
import com.overklassniy.stankinschedule.news.widget.domain.usecase.NewsWidgetUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Точка входа Hilt для получения зависимостей в Glance-компонентах виджета новостей.
 *
 * GlanceAppWidget создаётся системой, а не через Hilt, поэтому для доступа
 * к зависимостям используется EntryPointAccessors.fromApplication().
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface NewsGlanceEntryPoint {

    /**
     * Предоставляет use case для загрузки новостей в виджет.
     *
     * @return Экземпляр [NewsWidgetUseCase].
     */
    fun newsWidgetUseCase(): NewsWidgetUseCase

    /**
     * Предоставляет сервис отчётности об ошибках для репортинга сбоев рендеринга виджета.
     *
     * @return Экземпляр [CrashReporter].
     */
    fun crashReporter(): CrashReporter
}
