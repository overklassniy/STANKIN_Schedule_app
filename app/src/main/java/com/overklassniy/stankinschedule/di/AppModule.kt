package com.overklassniy.stankinschedule.di

import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.data.FirebaseLoggerAnalytics
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    @Singleton
    fun provideAnalytics(analytics: FirebaseLoggerAnalytics): LoggerAnalytics

}