package com.overklassniy.stankinschedule.schedule.repository.data.di

import android.content.Context
import com.overklassniy.stankinschedule.schedule.repository.data.db.RepositoryDao
import com.overklassniy.stankinschedule.schedule.repository.data.db.RepositoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt модуль для предоставления зависимостей базы данных репозитория расписаний.
 *
 * Отвечает за создание и внедрение экземпляров [RepositoryDatabase] и [RepositoryDao].
 */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
object RepositoryDatabaseModule {

    /**
     * Создает и предоставляет экземпляр базы данных [RepositoryDatabase].
     *
     * @param context Контекст приложения.
     * @return Экземпляр базы данных [RepositoryDatabase].
     */
    @Singleton
    @Provides
    fun provideRepositoryDatabase(
        @ApplicationContext context: Context,
    ): RepositoryDatabase = RepositoryDatabase.database(context)

    /**
     * Предоставляет Data Access Object (DAO) для работы с репозиторием.
     *
     * @param db Экземпляр базы данных [RepositoryDatabase].
     * @return Объект доступа к данным [RepositoryDao].
     */
    @Singleton
    @Provides
    fun provideRepositoryDao(db: RepositoryDatabase): RepositoryDao = db.repository()
}