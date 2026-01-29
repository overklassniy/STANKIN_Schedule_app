package com.overklassniy.stankinschedule.schedule.core.domain.model

import org.joda.time.DateTime

/**
 * Информация о расписании.
 *
 * @property scheduleName Название расписания.
 * @property lastUpdate Дата последнего обновления.
 * @property synced Флаг синхронизации с сервером.
 * @property position Позиция в списке расписаний.
 * @property id Уникальный идентификатор.
 */
class ScheduleInfo(
    val scheduleName: String,
    var lastUpdate: DateTime? = null,
    var synced: Boolean = false,
    var position: Int = 0,
    val id: Long = 0,
) {

    /**
     * Создает копию объекта с возможностью изменения полей.
     *
     * @param scheduleName Новое название (опционально).
     * @param lastUpdate Новая дата обновления (опционально).
     * @param synced Новый флаг синхронизации (опционально).
     * @param position Новая позиция (опционально).
     * @return Новый экземпляр [ScheduleInfo].
     */
    fun copy(
        scheduleName: String = this.scheduleName,
        lastUpdate: DateTime? = this.lastUpdate,
        synced: Boolean = this.synced,
        position: Int = this.position,
    ): ScheduleInfo {
        return ScheduleInfo(
            scheduleName = scheduleName,
            lastUpdate = lastUpdate,
            synced = synced,
            position = position,
            id = id
        )
    }
}