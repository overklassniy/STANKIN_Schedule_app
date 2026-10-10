package com.overklassniy.stankinschedule.core.ui.theme

import com.overklassniy.stankinschedule.core.domain.settings.ApplicationPreference
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Точка входа Hilt для получения [ApplicationPreference] без ViewModel.
 *
 * Используется внутри [AppTheme] для доступа к сохранённой теме оформления
 * через LocalContext, не требуя передачи параметра в каждую Activity.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppThemeEntryPoint {
    /**
     * Предоставляет экземпляр [ApplicationPreference].
     */
    fun applicationPreference(): ApplicationPreference
}
