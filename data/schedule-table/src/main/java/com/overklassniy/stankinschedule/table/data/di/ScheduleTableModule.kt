package com.overklassniy.stankinschedule.table.data.di

import com.overklassniy.stankinschedule.schedule.table.domain.repository.AndroidPublicProvider
import com.overklassniy.stankinschedule.schedule.table.domain.repository.AndroidTableCreator
import com.overklassniy.stankinschedule.table.data.repository.AndroidPublicProviderImpl
import com.overklassniy.stankinschedule.table.data.repository.AndroidTableCreatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface ScheduleTableModule {

    @Binds
    @ViewModelScoped
    fun provideSchedulePreference(provider: AndroidPublicProviderImpl): AndroidPublicProvider

    @Binds
    @ViewModelScoped
    fun provideTableCreator(creator: AndroidTableCreatorImpl): AndroidTableCreator
}