package com.overklassniy.stankinschedule.schedule.core.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Основной UseCase для управления расписаниями.
 *
 * Содержит бизнес-логику создания, получения, обновления и удаления расписаний.
 */
class ScheduleUseCase @Inject constructor(
    private val storage: ScheduleStorage,
) {

    /**
     * Создает новое расписание, если расписание с таким именем не существует.
     *
     * @param schedule Модель расписания для создания
     * @return Flow с true, если расписание создано, false если уже существует
     */
    fun createSchedule(schedule: ScheduleModel): Flow<Boolean> = flow {
        if (storage.isScheduleExist(schedule.info.scheduleName)) {
            emit(false)
            return@flow
        }

        storage.saveSchedule(schedule)
        emit(true)
    }.flowOn(Dispatchers.IO)

    /**
     * Проверяет, существует ли расписание с указанным именем.
     *
     * @param scheduleName Название расписания
     * @return true, если расписание существует, иначе false
     */
    suspend fun isScheduleExists(scheduleName: String) =
        storage.isScheduleExist(scheduleName)

    /**
     * Создает пустое расписание с указанным именем.
     *
     * @param scheduleName Название расписания
     * @return Flow с true, если расписание создано, false если уже существует
     */
    fun createEmptySchedule(scheduleName: String): Flow<Boolean> = flow {
        if (storage.isScheduleExist(scheduleName)) {
            emit(false)
            return@flow
        }

        val info = ScheduleInfo(scheduleName)
        val model = ScheduleModel(info)
        storage.saveSchedule(model)

        emit(true)
    }.flowOn(Dispatchers.IO)

    /**
     * Возвращает список всех расписаний.
     *
     * @return Flow со списком информации о расписаниях
     */
    fun schedules(): Flow<List<ScheduleInfo>> =
        storage.schedules()
            .flowOn(Dispatchers.IO)

    /**
     * Возвращает полную модель расписания по ID.
     *
     * @param scheduleId ID расписания
     * @return Flow с моделью расписания или null
     */
    fun scheduleModel(scheduleId: Long): Flow<ScheduleModel?> =
        storage.scheduleModel(scheduleId)
            .flowOn(Dispatchers.IO)

    /**
     * Удаляет расписание по ID.
     *
     * @param scheduleId ID расписания для удаления
     */
    suspend fun removeSchedule(scheduleId: Long) = withContext(Dispatchers.IO) {
        storage.removeSchedule(scheduleId)
    }

    /**
     * Переименовывает расписание, если новое имя не занято.
     *
     * @param scheduleId ID расписания
     * @param scheduleName Новое название расписания
     * @return Flow с true, если переименование успешно, false если имя занято
     */
    fun renameSchedule(scheduleId: Long, scheduleName: String): Flow<Boolean> = flow {
        if (storage.isScheduleExist(scheduleName)) {
            emit(false)
            return@flow
        }

        storage.renameSchedule(scheduleId, scheduleName)
        emit(true)

    }.flowOn(Dispatchers.IO)

    /**
     * Обновляет позиции расписаний в списке.
     *
     * @param list Список расписаний с новыми позициями
     */
    suspend fun updatePositions(list: List<ScheduleInfo>) {
        val newList = list.mapIndexed { index, scheduleInfo -> scheduleInfo.copy(position = index) }
        storage.updateSchedules(newList)
    }

    /**
     * Удаляет несколько расписаний.
     *
     * @param schedules Список расписаний для удаления
     */
    suspend fun removeSchedules(schedules: List<ScheduleInfo>) {
        storage.removeSchedules(schedules)
    }
}