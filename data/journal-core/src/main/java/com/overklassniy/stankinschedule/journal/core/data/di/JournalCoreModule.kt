package com.overklassniy.stankinschedule.journal.core.data.di

import com.google.gson.GsonBuilder
import com.overklassniy.stankinschedule.journal.core.data.api.ModuleJournalAPI
import com.overklassniy.stankinschedule.journal.core.data.repository.JournalPagingRepositoryImpl
import com.overklassniy.stankinschedule.journal.core.data.repository.JournalPreferenceImpl
import com.overklassniy.stankinschedule.journal.core.data.repository.JournalRepositoryImpl
import com.overklassniy.stankinschedule.journal.core.data.repository.JournalSecureRepositoryImpl
import com.overklassniy.stankinschedule.journal.core.data.repository.JournalServiceRepositoryImpl
import com.overklassniy.stankinschedule.journal.core.data.repository.JournalStorageRepositoryImpl
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalPagingRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalPreference
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalSecureRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalStorageRepository
import com.overklassniy.stankinschedule.journal.core.domain.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Dagger модуль для настройки и предоставления зависимостей модуля "Журнал".
 * Этот модуль устанавливается как в [ViewModelComponent], так и в [SingletonComponent],
 * чтобы зависимости были доступны на разных уровнях жизненного цикла.
 */
@Module
@InstallIn(ViewModelComponent::class, SingletonComponent::class)
@Suppress("Unused")
object JournalCoreModule {

    /**
     * Предоставляет настроенный экземпляр API модульного журнала.
     * Создает Retrofit клиент с базовым URL и конвертером Gson.
     *
     * @param client ОкHttp клиент, предоставляемый основным модулем (CoreModule)
     * @return Интерфейс [ModuleJournalAPI] для выполнения сетевых запросов
     */
    @Provides
    fun provideModuleJournalService(client: OkHttpClient): ModuleJournalAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.MODULE_JOURNAL_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(client)
            .build()
            .create(ModuleJournalAPI::class.java)
    }

    /**
     * Предоставляет репозиторий для работы с сетевым сервисом журнала.
     *
     * @param repository Реализация репозитория [JournalServiceRepositoryImpl]
     * @return Интерфейс репозитория [JournalServiceRepository]
     */
    @Provides
    fun provideServiceRepository(
        repository: JournalServiceRepositoryImpl,
    ): JournalServiceRepository = repository

    /**
     * Предоставляет репозиторий для работы с локальным хранилищем (БД) журнала.
     *
     * @param repository Реализация репозитория [JournalStorageRepositoryImpl]
     * @return Интерфейс репозитория [JournalStorageRepository]
     */
    @Provides
    fun provideStorageRepository(
        repository: JournalStorageRepositoryImpl,
    ): JournalStorageRepository = repository

    /**
     * Предоставляет репозиторий для работы с защищенными данными (шифрование, токены).
     *
     * @param repository Реализация репозитория [JournalSecureRepositoryImpl]
     * @return Интерфейс репозитория [JournalSecureRepository]
     */
    @Provides
    fun provideSecureRepository(
        repository: JournalSecureRepositoryImpl,
    ): JournalSecureRepository = repository

    /**
     * Предоставляет основной репозиторий журнала, объединяющий логику работы с данными.
     *
     * @param repository Реализация репозитория [JournalRepositoryImpl]
     * @return Интерфейс репозитория [JournalRepository]
     */
    @Provides
    fun provideJournalRepository(
        repository: JournalRepositoryImpl,
    ): JournalRepository = repository

    /**
     * Предоставляет репозиторий для пагинации данных журнала.
     *
     * @param repository Реализация репозитория [JournalPagingRepositoryImpl]
     * @return Интерфейс репозитория [JournalPagingRepository]
     */
    @Provides
    fun providePagingRepository(
        repository: JournalPagingRepositoryImpl,
    ): JournalPagingRepository = repository

    /**
     * Предоставляет интерфейс для работы с настройками журнала.
     *
     * @param preference Реализация настроек [JournalPreferenceImpl]
     * @return Интерфейс настроек [JournalPreference]
     */
    @Provides
    fun provideJournalPreference(
        preference: JournalPreferenceImpl
    ): JournalPreference = preference
}