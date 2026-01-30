package com.overklassniy.stankinschedule.ical.data.di

import com.overklassniy.stankinschedule.ical.data.repository.ICalRepository
import com.overklassniy.stankinschedule.schedule.ical.domain.repository.ICalExporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Модуль Hilt для внедрения зависимостей, связанных с iCal.
 */
@Module
@InstallIn(ViewModelComponent::class)
@Suppress("Unused")
interface ICalModule {

    /**
     * Предоставляет реализацию репозитория для экспорта расписания в iCal.
     *
     * @param repository Реализация репозитория ICalRepository
     * @return ICalExporter Интерфейс для экспорта
     */
    @Binds
    @ViewModelScoped
    fun provideICalRepository(repository: ICalRepository): ICalExporter

}