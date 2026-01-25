package com.overklassniy.stankinschedule.schedule.core.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case для работы с парами в расписании.
 */
class PairUseCase @Inject constructor(
    private val storage: ScheduleStorage,
) {
    /**
     * Получает пару по ID.
     *
     * @param pairId ID пары
     * @return Модель пары или null, если не найдена
     */
    suspend fun pair(pairId: Long): PairModel? {
        return storage.schedulePair(pairId).first()
    }

    /**
     * Удаляет пару из расписания.
     *
     * @param pair Модель пары для удаления
     */
    suspend fun deletePair(pair: PairModel) {
        storage.removeSchedulePair(pair)
    }

    /**
     * Изменяет пару в расписании или добавляет новую, если pair равен null.
     *
     * @param scheduleId ID расписания
     * @param pair Существующая пара для замены (null для добавления новой)
     * @param newPair Новая пара
     * @throws IllegalArgumentException если расписание не найдено
     */
    suspend fun changePair(scheduleId: Long, pair: PairModel?, newPair: PairModel) {
        val schedule = storage.scheduleModel(scheduleId).first()
            ?: throw IllegalArgumentException("Schedule $scheduleId not exist")

        schedule.changePair(pair, newPair)
        storage.saveSchedule(schedule, replaceExist = true)
    }
}