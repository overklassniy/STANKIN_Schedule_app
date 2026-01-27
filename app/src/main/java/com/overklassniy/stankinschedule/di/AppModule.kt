package com.overklassniy.stankinschedule.di

import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.data.FirebaseLoggerAnalytics
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger модуль для предоставления зависимостей на уровне всего приложения.
 *
 * Этот модуль устанавливается в [SingletonComponent], что означает, что
 * предоставляемые зависимости будут жить столько же, сколько и само приложение,
 * и будут единственными экземплярами (синглтонами).
 */
@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    /**
     * Предоставляет реализацию интерфейса [LoggerAnalytics].
     *
     * Использует [FirebaseLoggerAnalytics] в качестве конкретной реализации для аналитики и логирования.
     * Благодаря аннотации [Binds], Dagger автоматически связывает интерфейс с реализацией
     * без необходимости писать ручной код создания объекта.
     *
     * @param analytics Реализация аналитики через Firebase ([FirebaseLoggerAnalytics]).
     *                  Dagger сам найдет, как создать этот объект, так как у него есть конструктор с @Inject.
     * @return Экземпляр [LoggerAnalytics], который можно внедрять в другие классы.
     */
    @Suppress("unused")
    @Binds
    @Singleton
    fun provideAnalytics(analytics: FirebaseLoggerAnalytics): LoggerAnalytics

}