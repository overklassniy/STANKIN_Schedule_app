package com.overklassniy.stankinschedule.schedule.widget.ui.glance

import com.overklassniy.stankinschedule.core.domain.repository.CrashReporter
import com.overklassniy.stankinschedule.schedule.widget.domain.usecase.ScheduleWidgetUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Точка входа Hilt для получения зависимостей в Glance-компонентах.
 *
 * GlanceAppWidget создаётся системой, а не через Hilt, поэтому для доступа
 * к зависимостям используется EntryPointAccessors.fromApplication().
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface ScheduleGlanceEntryPoint {

    /**
     * Предоставляет use case для загрузки данных расписания в виджет.
     *
     * @return Экземпляр [ScheduleWidgetUseCase].
     */
    fun scheduleWidgetUseCase(): ScheduleWidgetUseCase

    /**
     * Предоставляет сервис отчётности об ошибках для репортинга сбоев рендеринга виджета.
     *
     * @return Экземпляр [CrashReporter].
     */
    fun crashReporter(): CrashReporter
}