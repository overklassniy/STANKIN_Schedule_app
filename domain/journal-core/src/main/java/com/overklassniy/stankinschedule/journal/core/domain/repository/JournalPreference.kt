package com.overklassniy.stankinschedule.journal.core.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс для доступа к настройкам журнала.
 *
 * Позволяет управлять параметрами, специфичными для модуля журнала (например, фоновое обновление).
 */
interface JournalPreference {

    /**
     * Возвращает поток данных о состоянии разрешения автоматического обновления оценок.
     *
     * @return [Flow] с булевым значением: `true` — обновление разрешено, `false` — запрещено.
     */
    fun isUpdateMarksAllow(): Flow<Boolean>

    /**
     * Устанавливает разрешение на автоматическое обновление оценок.
     *
     * @param allow `true` для разрешения, `false` для запрета.
     */
    suspend fun setUpdateMarksAllow(allow: Boolean)
}