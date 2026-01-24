package com.overklassniy.stankinschedule.schedule.repository.data.di

import com.overklassniy.stankinschedule.schedule.repository.data.repository.MoodleLoaderService
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.ScheduleLoaderService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryNetworkModule {

    @Provides
    @Singleton
    fun provideLoaderService(service: MoodleLoaderService): ScheduleLoaderService = service
}