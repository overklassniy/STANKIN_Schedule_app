package com.overklassniy.stankinschedule.schedule.repository.data.di

import com.overklassniy.stankinschedule.schedule.repository.data.repository.MoodleLoaderService
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.ScheduleLoaderService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt модуль для предоставления сетевых зависимостей загрузчика расписания.
 *
 * Устанавливается в [SingletonComponent], что обеспечивает доступность зависимостей во всем приложении.
 */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
object RepositoryNetworkModule {

    /**
     * Предоставляет сервис загрузки расписания.
     *
     * @param service Реализация сервиса загрузки через Moodle [MoodleLoaderService].
     * @return Интерфейс [ScheduleLoaderService] для загрузки расписания.
     */
    @Provides
    @Singleton
    fun provideLoaderService(service: MoodleLoaderService): ScheduleLoaderService = service
}