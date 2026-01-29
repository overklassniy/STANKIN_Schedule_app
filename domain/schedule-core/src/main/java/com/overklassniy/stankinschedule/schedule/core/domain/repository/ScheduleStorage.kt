package com.overklassniy.stankinschedule.schedule.core.domain.repository

import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс хранилища расписаний (обычно локальная БД).
 *
 * Предоставляет методы для CRUD операций над расписаниями и парами.
 */
interface ScheduleStorage {

    /**
     * Возвращает поток со списком всех сохраненных расписаний.
     *
     * @return [Flow] со списком [ScheduleInfo].
     */
    fun schedules(): Flow<List<ScheduleInfo>>

    /**
     * Возвращает поток с информацией о конкретном расписании по ID.
     *
     * @param scheduleId Идентификатор расписания.
     * @return [Flow] с [ScheduleInfo] или null, если не найдено.
     */
    fun schedule(scheduleId: Long): Flow<ScheduleInfo?>

    /**
     * Возвращает поток с полной моделью расписания (включая пары).
     *
     * @param scheduleId Идентификатор расписания.
     * @return [Flow] с [ScheduleModel] или null, если не найдено.
     */
    fun scheduleModel(scheduleId: Long): Flow<ScheduleModel?>

    /**
     * Возвращает поток с конкретной парой по ID.
     *
     * @param pairId Идентификатор пары.
     * @return [Flow] с [PairModel] или null, если не найдено.
     */
    fun schedulePair(pairId: Long): Flow<PairModel?>

    /**
     * Сохраняет расписание в хранилище.
     *
     * @param model Модель расписания для сохранения.
     * @param replaceExist Флаг перезаписи существующего расписания с таким же именем.
     * @return ID сохраненного расписания.
     */
    suspend fun saveSchedule(model: ScheduleModel, replaceExist: Boolean = false): Long

    /**
     * Проверяет существование расписания с заданным именем.
     *
     * @param scheduleName Название расписания.
     * @return true, если расписание существует.
     */
    suspend fun isScheduleExist(scheduleName: String): Boolean

    /**
     * Обновляет список информации о расписаниях (например, порядок или метаданные).
     *
     * @param schedules Список [ScheduleInfo] для обновления.
     */
    suspend fun updateSchedules(schedules: List<ScheduleInfo>)

    /**
     * Удаляет расписание по ID.
     *
     * @param id Идентификатор расписания.
     */
    suspend fun removeSchedule(id: Long)

    /**
     * Удаляет список расписаний.
     *
     * @param schedules Список [ScheduleInfo] для удаления.
     */
    suspend fun removeSchedules(schedules: List<ScheduleInfo>)

    /**
     * Удаляет конкретную пару из расписания.
     *
     * @param pair Модель пары для удаления.
     */
    suspend fun removeSchedulePair(pair: PairModel)

    /**
     * Переименовывает расписание.
     *
     * @param id Идентификатор расписания.
     * @param scheduleName Новое название.
     */
    suspend fun renameSchedule(id: Long, scheduleName: String)
}