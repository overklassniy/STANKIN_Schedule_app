package com.overklassniy.stankinschedule.news.widget.data.di

import com.overklassniy.stankinschedule.news.widget.domain.repository.NewsWidgetPreference
import com.overklassniy.stankinschedule.news.widget.data.repository.NewsWidgetPreferenceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

/**
 * Hilt-модуль для предоставления зависимостей виджета новостей.
 *
 * Устанавливается в [SingletonComponent] и [ViewModelComponent].
 */
@Module
@InstallIn(SingletonComponent::class, ViewModelComponent::class)
@Suppress("Unused")
interface NewsWidgetModule {

    /**
     * Связывает реализацию настроек виджета [NewsWidgetPreferenceImpl] с интерфейсом [NewsWidgetPreference].
     *
     * @param pref Реализация настроек.
     * @return Интерфейс для работы с настройками виджета.
     */
    @Binds
    fun providePreference(pref: NewsWidgetPreferenceImpl): NewsWidgetPreference
}