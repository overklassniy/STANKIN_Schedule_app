package com.overklassniy.stankinschedule.parser.data.di

import com.overklassniy.stankinschedule.parser.data.repository.PDFRepositoryImpl
import com.overklassniy.stankinschedule.schedule.parser.domain.repository.PDFRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt модуль для предоставления зависимостей, связанных с парсингом расписания.
 *
 * Отвечает за связывание реализаций репозиториев с их интерфейсами.
 */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("Unused")
interface ParserModule {

    /**
     * Связывает реализацию [PDFRepositoryImpl] с интерфейсом [PDFRepository].
     *
     * Позволяет внедрять [PDFRepository] в другие классы (например, UseCase),
     * скрывая конкретную реализацию парсера PDF.
     *
     * @param repository Реализация репозитория парсера PDF.
     * @return Интерфейс репозитория для работы с PDF.
     */
    @Binds
    @Singleton
    fun provideParseRepository(repository: PDFRepositoryImpl): PDFRepository

}