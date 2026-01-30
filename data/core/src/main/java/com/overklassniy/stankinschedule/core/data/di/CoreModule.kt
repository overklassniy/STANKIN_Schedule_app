package com.overklassniy.stankinschedule.core.data.di

import android.content.Context
import android.content.pm.ApplicationInfo
import com.overklassniy.stankinschedule.core.data.api.GitHubApi
import com.overklassniy.stankinschedule.core.data.preference.PreferenceManager
import com.overklassniy.stankinschedule.core.data.repository.UpdateRepositoryImpl
import com.overklassniy.stankinschedule.core.domain.repository.UpdateRepository
import com.overklassniy.stankinschedule.core.domain.settings.PreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dagger модуль для предоставления основных зависимостей слоя данных (Core Data Layer).
 * Этот модуль устанавливается в [SingletonComponent] и живет в течение всего времени работы приложения.
 */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("Unused")
object CoreModule {

    // Размер кэша для HTTP запросов (10 МБ)
    private const val CACHE_SIZE = 10L * 1024 * 1024

    // Тайм-аут для сетевых соединений в секундах
    private const val TIMEOUT_SECONDS = 30L

    /**
     * Предоставляет реализацию репозитория настроек.
     * Связывает интерфейс [PreferenceRepository] с реализацией [PreferenceManager].
     *
     * @param manager Реализация менеджера настроек [PreferenceManager]
     * @return Интерфейс для работы с настройками [PreferenceRepository]
     */
    @Provides
    @Singleton
    fun providePreferenceManager(manager: PreferenceManager): PreferenceRepository = manager

    /**
     * Предоставляет сконфигурированный HTTP-клиент [OkHttpClient].
     * Клиент настроен с кэшированием, тайм-аутами и логированием (в debug режиме).
     *
     * Алгоритм:
     * 1. Определяет режим отладки приложения.
     * 2. Создает директорию для кэша HTTP запросов.
     * 3. Настраивает тайм-ауты соединения, чтения и записи.
     * 4. В режиме отладки добавляет интерцептор логирования тела запросов.
     *
     * @param context Контекст приложения для доступа к кэш-директории и информации о сборке
     * @return Сконфигурированный экземпляр HTTP-клиента [OkHttpClient]
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        // Проверяем, запущено ли приложение в режиме отладки
        val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        // Настройка кэша
        val cacheDir = File(context.cacheDir, "http_cache")
        val cache = Cache(cacheDir, CACHE_SIZE)

        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .apply {
                // Добавляем логирование только для debug сборок
                if (isDebug) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    /**
     * Предоставляет API для работы с GitHub Releases.
     *
     * @param okHttpClient HTTP-клиент для запросов
     * @return Реализация [GitHubApi]
     */
    @Provides
    @Singleton
    fun provideGitHubApi(okHttpClient: OkHttpClient): GitHubApi {
        return Retrofit.Builder()
            .baseUrl(GitHubApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }

    /**
     * Предоставляет репозиторий для проверки обновлений.
     *
     * @param impl Реализация репозитория
     * @return Интерфейс [UpdateRepository]
     */
    @Provides
    @Singleton
    fun provideUpdateRepository(impl: UpdateRepositoryImpl): UpdateRepository = impl
}