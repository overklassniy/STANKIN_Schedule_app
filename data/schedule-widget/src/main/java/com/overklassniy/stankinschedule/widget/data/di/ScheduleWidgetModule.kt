package com.overklassniy.stankinschedule.widget.data.di

import com.overklassniy.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import com.overklassniy.stankinschedule.widget.data.repository.ScheduleWidgetPreferenceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class, ServiceComponent::class)
interface ScheduleWidgetModule {

    @Binds
    // Unscoped
    fun providePreference(pref: ScheduleWidgetPreferenceImpl): ScheduleWidgetPreference
}