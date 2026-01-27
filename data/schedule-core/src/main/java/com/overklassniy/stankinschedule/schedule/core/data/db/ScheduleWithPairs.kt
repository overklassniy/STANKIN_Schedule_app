package com.overklassniy.stankinschedule.schedule.core.data.db

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Класс, представляющий отношение "один ко многим" между расписанием и его парами.
 * Используется Room для автоматической загрузки пар вместе с расписанием.
 *
 * @property schedule Сущность расписания.
 * @property pairs Список пар, связанных с этим расписанием.
 */
class ScheduleWithPairs(
    @Embedded
    val schedule: ScheduleEntity,
    @Relation(parentColumn = "id", entityColumn = "schedule_id")
    val pairs: List<PairEntity>,
)