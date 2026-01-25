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

@Module
@InstallIn(ViewModelComponent::class, SingletonComponent::class)
object JournalCoreModule {

    @Provides
    // Unscoped
    fun provideModuleJournalService(client: OkHttpClient): ModuleJournalAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.MODULE_JOURNAL_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(client)
            .build()
            .create(ModuleJournalAPI::class.java)
    }

    @Provides
    // Unscoped
    fun provideServiceRepository(
        repository: JournalServiceRepositoryImpl,
    ): JournalServiceRepository = repository

    @Provides
    // Unscoped
    fun provideStorageRepository(
        repository: JournalStorageRepositoryImpl,
    ): JournalStorageRepository = repository

    @Provides
    // Unscoped
    fun provideSecureRepository(
        repository: JournalSecureRepositoryImpl,
    ): JournalSecureRepository = repository

    @Provides
    // Unscoped
    fun provideJournalRepository(
        repository: JournalRepositoryImpl,
    ): JournalRepository = repository

    @Provides
    // Unscoped
    fun providePagingRepository(
        repository: JournalPagingRepositoryImpl,
    ): JournalPagingRepository = repository

    @Provides
    fun provideJournalPreference(
        preference: JournalPreferenceImpl
    ): JournalPreference = preference
}