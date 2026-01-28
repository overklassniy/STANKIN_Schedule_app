package com.overklassniy.stankinschedule.core.domain.repository

import com.overklassniy.stankinschedule.core.domain.model.AppUpdate

/**
 * Репозиторий для проверки обновлений приложения.
 */
interface UpdateRepository {

    /**
     * Проверяет наличие обновления.
     *
     * @param currentVersion Текущая версия приложения.
     * @return [AppUpdate] если есть обновление, null если версия актуальна.
     */
    suspend fun checkForUpdate(currentVersion: String): AppUpdate?
}