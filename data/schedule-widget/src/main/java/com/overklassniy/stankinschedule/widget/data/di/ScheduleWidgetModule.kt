package com.overklassniy.stankinschedule.widget.data.di

import com.overklassniy.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import com.overklassniy.stankinschedule.widget.data.repository.ScheduleWidgetPreferenceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

/**
 * Hilt модуль для предоставления зависимостей виджета расписания.
 *
 * Устанавливается в [SingletonComponent] и [ViewModelComponent].
 */
@Module
@InstallIn(SingletonComponent::class, ViewModelComponent::class)
@Suppress("Unused")
interface ScheduleWidgetModule {

    /**
     * Связывает реализацию настроек виджета [ScheduleWidgetPreferenceImpl] с интерфейсом [ScheduleWidgetPreference].
     *
     * @param pref Реализация настроек.
     * @return Интерфейс для работы с настройками виджета.
     */
    @Binds
    fun providePreference(pref: ScheduleWidgetPreferenceImpl): ScheduleWidgetPreference
}
