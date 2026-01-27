package com.overklassniy.stankinschedule.schedule.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) для работы с таблицей расписаний и пар.
 * Предоставляет методы для чтения, вставки, обновления и удаления данных.
 */
@Dao
interface ScheduleDao {

    /**
     * Получает список всех расписаний, отсортированных по позиции.
     *
     * @return Flow со списком [ScheduleEntity].
     */
    @Query("SELECT * FROM schedule_entities ORDER BY position ASC")
    fun getAllSchedules(): Flow<List<ScheduleEntity>>

    /**
     * Получает расписание вместе с его парами по ID.
     *
     * @param id ID расписания.
     * @return Flow с объектом [ScheduleWithPairs] или null, если не найдено.
     */
    @Transaction
    @Query("SELECT * FROM schedule_entities WHERE id == :id LIMIT 1")
    fun getScheduleWithPairs(id: Long): Flow<ScheduleWithPairs?>

    /**
     * Получает сущность расписания по имени (suspend функция).
     *
     * @param scheduleName Имя расписания.
     * @return Объект [ScheduleEntity] или null.
     */
    @Query("SELECT * FROM schedule_entities WHERE schedule_name = :scheduleName LIMIT 1")
    suspend fun getScheduleEntity(scheduleName: String): ScheduleEntity?

    /**
     * Получает сущность расписания по ID.
     *
     * @param id ID расписания.
     * @return Flow с объектом [ScheduleEntity] или null.
     */
    @Query("SELECT * FROM schedule_entities WHERE id = :id LIMIT 1")
    fun getScheduleEntity(id: Long): Flow<ScheduleEntity?>

    /**
     * Возвращает общее количество сохраненных расписаний.
     *
     * @return Количество расписаний.
     */
    @Query("SELECT COUNT(*) FROM schedule_entities")
    suspend fun getScheduleCount(): Int

    /**
     * Проверяет, существует ли расписание с заданным именем.
     *
     * @param scheduleName Имя расписания.
     * @return true, если существует, иначе false.
     */
    @Query("SELECT EXISTS(SELECT * FROM schedule_entities WHERE schedule_name = :scheduleName)")
    suspend fun isScheduleExist(scheduleName: String): Boolean

    /**
     * Получает пару по её ID.
     *
     * @param id ID пары.
     * @return Flow с объектом [PairEntity] или null.
     */
    @Query("SELECT * FROM schedule_pair_entities WHERE id == :id LIMIT 1")
    fun getPairEntity(id: Long): Flow<PairEntity?>

    /**
     * Вставляет новое расписание.
     *
     * @param schedule Сущность расписания.
     * @return ID вставленной записи.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertScheduleEntity(schedule: ScheduleEntity): Long

    /**
     * Обновляет данные одного расписания.
     *
     * @param schedule Обновленная сущность расписания.
     */
    @Update
    suspend fun updateScheduleItem(schedule: ScheduleEntity)

    /**
     * Обновляет список расписаний.
     *
     * @param scheduleEntities Список обновленных сущностей.
     */
    @Update
    suspend fun updateScheduleItems(scheduleEntities: List<ScheduleEntity>)

    /**
     * Вставляет список пар.
     *
     * @param pairs Список пар для вставки.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPairs(pairs: List<PairEntity>)

    /**
     * Удаляет пару по ID.
     *
     * @param pairId ID пары.
     */
    @Query("DELETE FROM schedule_pair_entities WHERE id = :pairId")
    suspend fun deletePairEntity(pairId: Long)

    /**
     * Удаляет все пары, относящиеся к определенному расписанию.
     *
     * @param scheduleId ID расписания.
     */
    @Query("DELETE FROM schedule_pair_entities WHERE schedule_id = :scheduleId")
    suspend fun deleteSchedulePairs(scheduleId: Long)

    /**
     * Удаляет расписание по ID.
     *
     * @param scheduleId ID расписания.
     */
    @Transaction
    @Query("DELETE FROM schedule_entities WHERE id = :scheduleId")
    suspend fun deleteSchedule(scheduleId: Long)

    /**
     * Удаляет список расписаний по их ID.
     *
     * @param scheduleIds Список ID расписаний для удаления.
     */
    @Query("DELETE FROM schedule_entities WHERE id IN (:scheduleIds)")
    suspend fun deleteSchedulesByIds(scheduleIds: List<Long>)
}