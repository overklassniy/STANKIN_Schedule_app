package com.overklassniy.stankinschedule.di

import com.overklassniy.stankinschedule.core.domain.repository.CrashReporter
import com.overklassniy.stankinschedule.core.domain.repository.ProductAnalytics
import com.overklassniy.stankinschedule.data.FirebaseProductAnalytics
import com.overklassniy.stankinschedule.data.SentryCrashReporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger модуль для предоставления зависимостей аналитики и краш-репортинга.
 *
 * Устанавливается в [SingletonComponent], предоставляемые зависимости
 * живут столько же, сколько само приложение.
 */
@Suppress("Unused")
@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    /**
     * Предоставляет реализацию [ProductAnalytics] через [FirebaseProductAnalytics].
     *
     * @param analytics Реализация продуктовой аналитики через Firebase.
     * @return Экземпляр [ProductAnalytics].
     */
    @Suppress("Unused")
    @Binds
    @Singleton
    fun provideProductAnalytics(analytics: FirebaseProductAnalytics): ProductAnalytics

    /**
     * Предоставляет реализацию [CrashReporter] через [SentryCrashReporter].
     *
     * @param crashReporter Реализация краш-репортинга через Sentry.
     * @return Экземпляр [CrashReporter].
     */
    @Suppress("Unused")
    @Binds
    @Singleton
    fun provideCrashReporter(crashReporter: SentryCrashReporter): CrashReporter
}