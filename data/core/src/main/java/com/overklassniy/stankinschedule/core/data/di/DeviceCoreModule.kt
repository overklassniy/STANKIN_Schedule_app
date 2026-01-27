package com.overklassniy.stankinschedule.core.data.di

import com.overklassniy.stankinschedule.core.data.repository.DeviceRepositoryImpl
import com.overklassniy.stankinschedule.core.domain.repository.DeviceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Dagger модуль для предоставления зависимостей, связанных с работой устройства.
 * Устанавливается в [ViewModelComponent], поэтому зависимости живут столько же, сколько и ViewModel.
 */
@Module
@InstallIn(ViewModelComponent::class)
@Suppress("unused")
interface DeviceCoreModule {

    /**
     * Связывает реализацию репозитория устройства с его интерфейсом.
     *
     * @param repository Реализация репозитория [DeviceRepositoryImpl]
     * @return Интерфейс репозитория для работы с устройством [DeviceRepository]
     */
    @Binds
    @ViewModelScoped
    fun provideDeviceRepository(repository: DeviceRepositoryImpl): DeviceRepository
}