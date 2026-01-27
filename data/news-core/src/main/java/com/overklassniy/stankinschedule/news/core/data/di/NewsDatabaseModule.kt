package com.overklassniy.stankinschedule.news.core.data.di

import android.content.Context
import com.overklassniy.stankinschedule.news.core.data.db.NewsDao
import com.overklassniy.stankinschedule.news.core.data.db.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt модуль для внедрения зависимостей, связанных с базой данных новостей.
 * Устанавливается в [SingletonComponent], что означает, что зависимости живут столько же, сколько и приложение.
 */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
object NewsDatabaseModule {

    /**
     * Предоставляет экземпляр базы данных новостей [NewsDatabase].
     * Создается как Singleton (единственный экземпляр на все приложение).
     *
     * @param context Контекст приложения (ApplicationContext).
     * @return Экземпляр базы данных [NewsDatabase].
     */
    @Singleton
    @Provides
    fun provideRepositoryDatabase(
        @ApplicationContext context: Context,
    ): NewsDatabase = NewsDatabase.database(context)

    /**
     * Предоставляет DAO (Data Access Object) для работы с новостями.
     * Создается как Singleton.
     *
     * @param db Экземпляр базы данных [NewsDatabase].
     * @return Экземпляр [NewsDao] для выполнения запросов к таблице новостей.
     */
    @Singleton
    @Provides
    fun provideRepositoryDao(db: NewsDatabase): NewsDao = db.news()

}