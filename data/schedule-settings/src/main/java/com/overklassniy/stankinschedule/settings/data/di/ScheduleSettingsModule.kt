package com.overklassniy.stankinschedule.settings.data.di

import com.overklassniy.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import com.overklassniy.stankinschedule.settings.data.repository.ScheduleDataStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt модуль для предоставления зависимостей настроек расписания.
 *
 * Отвечает за связывание реализаций с интерфейсами.
 */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
interface ScheduleSettingsModule {

    /**
     * Связывает реализацию хранилища настроек [ScheduleDataStore] с интерфейсом [SchedulePreference].
     *
     * @param preference Реализация хранилища настроек.
     * @return Интерфейс для работы с настройками расписания.
     */
    @Binds
    @Singleton
    fun provideSchedulePreference(preference: ScheduleDataStore): SchedulePreference
}