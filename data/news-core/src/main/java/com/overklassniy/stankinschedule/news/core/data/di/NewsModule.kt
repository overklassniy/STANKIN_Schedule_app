package com.overklassniy.stankinschedule.news.core.data.di

import com.google.gson.GsonBuilder
import com.overklassniy.stankinschedule.news.core.data.api.PostResponse
import com.overklassniy.stankinschedule.news.core.data.api.StankinDeanNewsAPI
import com.overklassniy.stankinschedule.news.core.data.api.StankinUniversityNewsAPI
import com.overklassniy.stankinschedule.news.core.data.repository.NewsPostRepositoryImpl
import com.overklassniy.stankinschedule.news.core.data.repository.NewsPreferenceRepositoryImpl
import com.overklassniy.stankinschedule.news.core.data.repository.NewsStorageRepositoryImpl
import com.overklassniy.stankinschedule.news.core.data.repository.UniversityNewsRepositoryImpl
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsPostRepository
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsPreferenceRepository
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsRemoteRepository
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsStorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

/**
 * Модуль Hilt для сетевых зависимостей модуля новостей.
 * Предоставляет экземпляры API сервисов.
 * Установлен в [SingletonComponent], чтобы клиенты жили все время работы приложения.
 */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
object NewsNetworkModule {

    /**
     * Предоставляет сервис API для основного сайта университета (stankin.ru).
     * Использует [ScalarsConverterFactory], так как сервер возвращает HTML строку.
     *
     * @param client OkHttpClient для выполнения запросов.
     * @return Экземпляр [StankinUniversityNewsAPI].
     */
    @Provides
    @Singleton
    fun provideUniversityNewsService(client: OkHttpClient): StankinUniversityNewsAPI {
        return Retrofit.Builder()
            .baseUrl(StankinUniversityNewsAPI.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(client)
            .build()
            .create(StankinUniversityNewsAPI::class.java)
    }

    /**
     * Предоставляет сервис API для сайта деканата (old.stankin.ru).
     * Использует [GsonConverterFactory] с кастомным десериализатором для [PostResponse.NewsPost].
     *
     * @param client OkHttpClient для выполнения запросов.
     * @return Экземпляр [StankinDeanNewsAPI].
     */
    @Provides
    @Singleton
    @Suppress("unused")
    fun provideDeanNewsService(client: OkHttpClient): StankinDeanNewsAPI {
        return Retrofit.Builder()
            .baseUrl(StankinDeanNewsAPI.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(
                            PostResponse.NewsPost::class.java, PostResponse.NewsPostDeserializer()
                        )
                        .create()
                )
            )
            .client(client)
            .build()
            .create(StankinDeanNewsAPI::class.java)
    }
}

/**
 * Модуль Hilt для зависимостей репозиториев модуля новостей.
 * Установлен в [ViewModelComponent], зависимости живут пока жива ViewModel.
 */
@Module
@InstallIn(ViewModelComponent::class)
@Suppress("unused")
object NewsModule {

    /**
     * Предоставляет реализацию репозитория для работы с локальным хранилищем (БД).
     *
     * @param repository Реализация [NewsStorageRepositoryImpl].
     * @return Интерфейс [NewsStorageRepository].
     */
    @Provides
    @ViewModelScoped
    fun provideNewsStorageRepository(
        repository: NewsStorageRepositoryImpl
    ): NewsStorageRepository = repository

    /**
     * Предоставляет реализацию репозитория для работы с удаленными данными (сеть).
     *
     * @param repository Реализация [UniversityNewsRepositoryImpl].
     * @return Интерфейс [NewsRemoteRepository].
     */
    @Provides
    @ViewModelScoped
    fun provideNewsRemoteRepository(
        repository: UniversityNewsRepositoryImpl
    ): NewsRemoteRepository = repository

    /**
     * Предоставляет реализацию репозитория для работы с настройками.
     *
     * @param repository Реализация [NewsPreferenceRepositoryImpl].
     * @return Интерфейс [NewsPreferenceRepository].
     */
    @Provides
    @ViewModelScoped
    fun provideNewsPreferenceRepository(
        repository: NewsPreferenceRepositoryImpl
    ): NewsPreferenceRepository = repository

    /**
     * Предоставляет реализацию репозитория для получения контента конкретной новости.
     *
     * @param repository Реализация [NewsPostRepositoryImpl].
     * @return Интерфейс [NewsPostRepository].
     */
    @Provides
    @ViewModelScoped
    fun provideNewsPostRepository(
        repository: NewsPostRepositoryImpl
    ): NewsPostRepository = repository
}