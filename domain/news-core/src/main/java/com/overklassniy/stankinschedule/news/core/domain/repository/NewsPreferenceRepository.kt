package com.overklassniy.stankinschedule.news.core.domain.repository

import org.joda.time.DateTime

/**
 * Репозиторий для хранения метаданных новостей (например, времени последнего обновления).
 */
interface NewsPreferenceRepository {

    /**
     * Обновляет время последней синхронизации новостей для подразделения.
     *
     * @param subdivision Идентификатор подразделения.
     * @param time Время обновления (по умолчанию текущее время).
     */
    fun updateNewsDateTime(subdivision: Int, time: DateTime = DateTime.now())

    /**
     * Возвращает время последней синхронизации новостей для подразделения.
     *
     * @param subdivision Идентификатор подразделения.
     * @return [DateTime] последнего обновления или null, если обновление еще не выполнялось.
     */
    fun currentNewsDateTime(subdivision: Int): DateTime?
}