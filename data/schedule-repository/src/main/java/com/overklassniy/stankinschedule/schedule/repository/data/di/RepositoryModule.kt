package com.overklassniy.stankinschedule.schedule.repository.data.di

import com.overklassniy.stankinschedule.schedule.repository.data.repository.MoodleRemoteService
import com.overklassniy.stankinschedule.schedule.repository.data.repository.RepositoryStorageImpl
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.RepositoryStorage
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.ScheduleRemoteService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt модуль для связывания реализаций репозиториев с их интерфейсами.
 *
 * Устанавливается в [ViewModelComponent], поэтому зависимости живут столько же, сколько и ViewModel.
 */
@Module
@InstallIn(ViewModelComponent::class)
@Suppress("unused")
interface RepositoryModule {

    /**
     * Связывает реализацию удаленного сервиса Moodle с интерфейсом [ScheduleRemoteService].
     *
     * @param service Реализация сервиса работы с Moodle [MoodleRemoteService].
     * @return Интерфейс для работы с удаленным сервисом расписания.
     */
    @Binds
    @ViewModelScoped
    fun provideRepositoryService(service: MoodleRemoteService): ScheduleRemoteService

    /**
     * Связывает реализацию локального хранилища репозитория с интерфейсом [RepositoryStorage].
     *
     * @param storage Реализация хранилища [RepositoryStorageImpl].
     * @return Интерфейс для работы с хранилищем репозитория.
     */
    @Binds
    @ViewModelScoped
    fun provideRepositoryStorage(storage: RepositoryStorageImpl): RepositoryStorage

}