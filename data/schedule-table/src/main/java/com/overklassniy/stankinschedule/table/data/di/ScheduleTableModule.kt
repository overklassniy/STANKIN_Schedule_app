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

/**
 * Hilt модуль для предоставления зависимостей, связанных с таблицей расписания и экспортом.
 *
 * Устанавливается в [ViewModelComponent].
 */
@Module
@InstallIn(ViewModelComponent::class)
@Suppress("unused")
interface ScheduleTableModule {

    /**
     * Связывает реализацию провайдера файлов [AndroidPublicProviderImpl] с интерфейсом [AndroidPublicProvider].
     *
     * @param provider Реализация провайдера.
     * @return Интерфейс для работы с URI и файлами.
     */
    @Binds
    @ViewModelScoped
    fun provideSchedulePreference(provider: AndroidPublicProviderImpl): AndroidPublicProvider

    /**
     * Связывает реализацию создателя таблицы [AndroidTableCreatorImpl] с интерфейсом [AndroidTableCreator].
     *
     * @param creator Реализация создателя таблицы.
     * @return Интерфейс для отрисовки расписания в таблицу (Bitmap/PDF).
     */
    @Binds
    @ViewModelScoped
    fun provideTableCreator(creator: AndroidTableCreatorImpl): AndroidTableCreator
}