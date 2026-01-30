package com.overklassniy.stankinschedule.schedule.viewer.data.di

import com.overklassniy.stankinschedule.schedule.viewer.data.repository.ScheduleViewerRepositoryImpl
import com.overklassniy.stankinschedule.schedule.viewer.domain.repository.ScheduleViewerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt модуль для предоставления зависимостей модуля просмотра расписания.
 *
 * Устанавливается в [ViewModelComponent].
 */
@Module
@InstallIn(ViewModelComponent::class)
@Suppress("Unused")
interface ScheduleViewerModule {

    /**
     * Связывает реализацию репозитория [ScheduleViewerRepositoryImpl] с интерфейсом [ScheduleViewerRepository].
     *
     * @param repository Реализация репозитория.
     * @return Интерфейс для работы с просмотром расписания (PagingSource, маппинг).
     */
    @Binds
    @ViewModelScoped
    fun provideScheduleSource(repository: ScheduleViewerRepositoryImpl): ScheduleViewerRepository
}