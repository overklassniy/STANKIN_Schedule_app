package com.overklassniy.stankinschedule.news.core.data.di

import com.google.gson.GsonBuilder
import com.overklassniy.stankinschedule.news.core.data.api.PostResponse
import com.overklassniy.stankinschedule.news.core.data.api.StankinNews2024API
import com.overklassniy.stankinschedule.news.core.data.api.StankinNewsAPI
import com.overklassniy.stankinschedule.news.core.data.api.StankinDeanNewsAPI
import com.overklassniy.stankinschedule.news.core.data.repository.NewsMediatorRepositoryImpl
import com.overklassniy.stankinschedule.news.core.data.repository.NewsPostRepositoryImpl
import com.overklassniy.stankinschedule.news.core.data.repository.NewsPreferenceRepositoryImpl
import com.overklassniy.stankinschedule.news.core.data.repository.NewsRemoteRepository2024Impl
import com.overklassniy.stankinschedule.news.core.data.repository.NewsStorageRepositoryImpl
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsMediatorRepository
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

@Module
@InstallIn(SingletonComponent::class)
object NewsNetworkModule {

    @Provides
    @Singleton
    fun provideNewsService(client: OkHttpClient): StankinNewsAPI {
        return Retrofit.Builder()
            .baseUrl(StankinNewsAPI.BASE_URL)
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
            .create(StankinNewsAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideNews2024Service(client: OkHttpClient): StankinNews2024API {
        return Retrofit.Builder()
            .baseUrl(StankinNews2024API.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(client)
            .build()
            .create(StankinNews2024API::class.java)
    }

    @Provides
    @Singleton
    fun provideOldNewsService(client: OkHttpClient): StankinDeanNewsAPI {
        return Retrofit.Builder()
            .baseUrl(StankinDeanNewsAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(StankinDeanNewsAPI::class.java)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object NewsModule {

    @Provides
    @ViewModelScoped
    fun provideNewsStorageRepository(
        repository: NewsStorageRepositoryImpl
    ): NewsStorageRepository = repository

    @Provides
    @ViewModelScoped
    fun provideNewsRemoteRepository(
        repository: NewsRemoteRepository2024Impl
    ): NewsRemoteRepository = repository

    @Provides
    @ViewModelScoped
    fun provideNewsMediatorRepository(
        repository: NewsMediatorRepositoryImpl
    ): NewsMediatorRepository = repository

    @Provides
    @ViewModelScoped
    fun provideNewsPreferenceRepository(
        repository: NewsPreferenceRepositoryImpl
    ): NewsPreferenceRepository = repository

    @Provides
    @ViewModelScoped
    fun provideNewsPostRepository(
        repository: NewsPostRepositoryImpl
    ): NewsPostRepository = repository
}