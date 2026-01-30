package com.overklassniy.stankinschedule.widget.data.di

import com.overklassniy.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import com.overklassniy.stankinschedule.widget.data.repository.ScheduleWidgetPreferenceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent

/**
 * Hilt модуль для предоставления зависимостей виджета расписания.
 *
 * Устанавливается в [ViewModelComponent] и [ServiceComponent].
 */
@Module
@InstallIn(ViewModelComponent::class, ServiceComponent::class)
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