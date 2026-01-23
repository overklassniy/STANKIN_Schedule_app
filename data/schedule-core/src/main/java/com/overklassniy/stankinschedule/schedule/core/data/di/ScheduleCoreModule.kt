package com.overklassniy.stankinschedule.schedule.core.data.di

import com.overklassniy.stankinschedule.schedule.core.data.repository.ScheduleDeviceRepositoryImpl
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleDeviceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface ScheduleCoreModule {

    @Binds
    @ViewModelScoped
    fun provideDeviceRepository(repositoryImpl: ScheduleDeviceRepositoryImpl): ScheduleDeviceRepository

}