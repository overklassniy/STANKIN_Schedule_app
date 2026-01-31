package com.overklassniy.stankinschedule.core.domain.repository

/**
 * Репозиторий для проверки доступности приложения в Google Play.
 */
interface GooglePlayAvailabilityRepository {

    /**
     * Проверяет, опубликовано ли приложение в Google Play.
     *
     * @return `true`, если страница приложения в Play Store доступна, иначе `false`.
     */
    suspend fun isAppAvailableOnPlayStore(): Boolean
}
