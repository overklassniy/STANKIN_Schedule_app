package com.overklassniy.stankinschedule.schedule.viewer.data.di

import com.overklassniy.stankinschedule.schedule.viewer.data.repository.ScheduleViewerRepositoryImpl
import com.overklassniy.stankinschedule.schedule.viewer.data.source.EmployeeDataSource
import com.overklassniy.stankinschedule.schedule.viewer.domain.repository.ScheduleViewerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
/**
 * Hilt модуль для предоставления зависимостей модуля просмотра расписания.
 *
 * Устанавливается в [ViewModelComponent].
 */
@Module
@InstallIn(ViewModelComponent::class)
@Suppress("Unused")
abstract class ScheduleViewerModule {

    @Binds
    @ViewModelScoped
    abstract fun provideScheduleSource(repository: ScheduleViewerRepositoryImpl): ScheduleViewerRepository

    companion object {
        @Provides
        fun provideEmployeeDataSource(
            @ApplicationContext context: android.content.Context
        ): EmployeeDataSource = EmployeeDataSource(context)
    }
}