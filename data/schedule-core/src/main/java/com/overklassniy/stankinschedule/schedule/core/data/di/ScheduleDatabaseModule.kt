package com.overklassniy.stankinschedule.schedule.core.data.di

import android.content.Context
import com.overklassniy.stankinschedule.schedule.core.data.db.ScheduleDao
import com.overklassniy.stankinschedule.schedule.core.data.db.ScheduleDatabase
import com.overklassniy.stankinschedule.schedule.core.data.repository.ScheduleStorageImpl
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger модуль для предоставления зависимостей базы данных расписания.
 * Устанавливается в [SingletonComponent], чтобы зависимости жили в течение всего времени работы приложения.
 */
@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object ScheduleDatabaseModule {

    /**
     * Предоставляет экземпляр базы данных расписания.
     *
     * @param context Контекст приложения.
     * @return Экземпляр [ScheduleDatabase].
     */
    @Singleton
    @Provides
    fun provideScheduleDatabase(
        @ApplicationContext context: Context,
    ): ScheduleDatabase = ScheduleDatabase.database(context)

    /**
     * Предоставляет DAO для работы с расписанием.
     *
     * @param db Экземпляр базы данных [ScheduleDatabase].
     * @return Интерфейс [ScheduleDao].
     */
    @Singleton
    @Provides
    fun provideScheduleDao(db: ScheduleDatabase): ScheduleDao = db.schedule()

    /**
     * Предоставляет реализацию хранилища расписания.
     *
     * @param storage Реализация [ScheduleStorageImpl].
     * @return Интерфейс [ScheduleStorage].
     */
    @Singleton
    @Provides
    fun provideScheduleStorage(storage: ScheduleStorageImpl): ScheduleStorage = storage
}