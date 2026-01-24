package com.overklassniy.stankinschedule.parser.data.di

import com.overklassniy.stankinschedule.parser.data.repository.PDFRepositoryImpl
import com.overklassniy.stankinschedule.schedule.parser.domain.repository.PDFRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
interface ParserModule {

    @Binds
    @Singleton
    fun provideParseRepository(repository: PDFRepositoryImpl): PDFRepository

}