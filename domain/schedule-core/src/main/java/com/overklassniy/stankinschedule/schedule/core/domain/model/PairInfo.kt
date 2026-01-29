package com.overklassniy.stankinschedule.schedule.core.domain.model

/**
 * Дополнительная информация о паре.
 *
 * Используется для связи с базой данных (идентификаторы).
 *
 * @property scheduleId Идентификатор расписания, к которому относится пара.
 * @property id Уникальный идентификатор пары.
 */
data class PairInfo(
    val scheduleId: Long = 0,
    val id: Long = 0
)