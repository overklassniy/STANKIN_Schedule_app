package com.overklassniy.stankinschedule.schedule.viewer.data.di

import com.overklassniy.stankinschedule.schedule.viewer.data.repository.ScheduleViewerRepositoryImpl
import com.overklassniy.stankinschedule.schedule.viewer.domain.repository.ScheduleViewerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface ScheduleViewerModule {

    @Binds
    @ViewModelScoped
    fun provideScheduleSource(repository: ScheduleViewerRepositoryImpl): ScheduleViewerRepository
}