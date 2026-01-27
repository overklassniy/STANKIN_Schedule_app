package com.overklassniy.stankinschedule.schedule.core.data.repository

import androidx.room.withTransaction
import com.overklassniy.stankinschedule.schedule.core.data.db.ScheduleDao
import com.overklassniy.stankinschedule.schedule.core.data.db.ScheduleDatabase
import com.overklassniy.stankinschedule.schedule.core.data.mapper.toEntity
import com.overklassniy.stankinschedule.schedule.core.data.mapper.toInfo
import com.overklassniy.stankinschedule.schedule.core.data.mapper.toPairModel
import com.overklassniy.stankinschedule.schedule.core.data.mapper.toScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

/**
 * Реализация хранилища расписания [ScheduleStorage].
 * Использует Room Database для сохранения данных.
 *
 * @property dbProvider Провайдер базы данных расписания.
 * @property daoProvider Провайдер DAO расписания.
 */
class ScheduleStorageImpl @Inject constructor(
    private val dbProvider: Provider<ScheduleDatabase>,
    private val daoProvider: Provider<ScheduleDao>,
) : ScheduleStorage {

    private val db: ScheduleDatabase
        get() = dbProvider.get()

    private val dao: ScheduleDao
        get() = daoProvider.get()

    /**
     * Получает список всех сохраненных расписаний.
     *
     * @return Flow со списком информации о расписаниях [ScheduleInfo].
     */
    override fun schedules(): Flow<List<ScheduleInfo>> {
        return dao.getAllSchedules().map { data -> data.map { it.toInfo() } }
    }

    /**
     * Получает информацию о расписании по ID.
     *
     * @param scheduleId Идентификатор расписания.
     * @return Flow с информацией о расписании или null, если не найдено.
     */
    override fun schedule(scheduleId: Long): Flow<ScheduleInfo?> {
        return dao.getScheduleEntity(scheduleId).map { it?.toInfo() }
    }

    /**
     * Получает полную модель расписания (включая пары) по ID.
     *
     * @param scheduleId Идентификатор расписания.
     * @return Flow с моделью расписания или null, если не найдено.
     */
    override fun scheduleModel(scheduleId: Long): Flow<ScheduleModel?> {
        return dao.getScheduleWithPairs(scheduleId).map { it?.toScheduleModel() }
    }

    /**
     * Получает информацию о конкретной паре по ID.
     *
     * @param pairId Идентификатор пары.
     * @return Flow с моделью пары или null, если не найдено.
     */
    override fun schedulePair(pairId: Long): Flow<PairModel?> {
        return dao.getPairEntity(pairId).map { it?.toPairModel() }
    }

    /**
     * Сохраняет расписание в базу данных.
     * Выполняется в транзакции.
     *
     * @param model Модель расписания для сохранения.
     * @param replaceExist Флаг, указывающий нужно ли заменять существующее расписание (по имени).
     * @return ID сохраненного расписания.
     */
    override suspend fun saveSchedule(model: ScheduleModel, replaceExist: Boolean): Long {
        return db.withTransaction {
            if (replaceExist) {
                val prevItem = dao.getScheduleEntity(model.info.scheduleName)
                if (prevItem != null) {
                    dao.deleteSchedulePairs(prevItem.id)

                    val pairEntities = model.map { pair -> pair.toEntity(prevItem.id) }
                    dao.insertPairs(pairEntities)

                    return@withTransaction prevItem.id
                }
            }

            val lastPosition = dao.getScheduleCount()
            val scheduleEntity = model.toEntity(position = lastPosition)
            val scheduleId = dao.insertScheduleEntity(scheduleEntity)

            val pairEntities = model.map { pair -> pair.toEntity(scheduleId) }
            dao.insertPairs(pairEntities)

            return@withTransaction scheduleId
        }
    }

    /**
     * Проверяет существование расписания с заданным именем.
     *
     * @param scheduleName Имя расписания.
     * @return true, если расписание существует, иначе false.
     */
    override suspend fun isScheduleExist(scheduleName: String): Boolean {
        return dao.isScheduleExist(scheduleName)
    }

    /**
     * Обновляет информацию о списке расписаний.
     *
     * @param schedules Список обновленной информации о расписаниях.
     */
    override suspend fun updateSchedules(schedules: List<ScheduleInfo>) {
        db.withTransaction {
            dao.updateScheduleItems(schedules.map { it.toEntity() })
        }
    }

    /**
     * Удаляет расписание по ID.
     *
     * @param id Идентификатор расписания.
     */
    override suspend fun removeSchedule(id: Long) {
        db.withTransaction {
            dao.deleteSchedule(id)
        }
    }

    /**
     * Удаляет список расписаний.
     *
     * @param schedules Список расписаний для удаления.
     */
    override suspend fun removeSchedules(schedules: List<ScheduleInfo>) {
        if (schedules.isEmpty()) return
        db.withTransaction {
            val ids = schedules.map { it.id }
            dao.deleteSchedulesByIds(ids)
        }
    }

    /**
     * Удаляет пару из расписания.
     *
     * @param pair Модель пары для удаления.
     */
    override suspend fun removeSchedulePair(pair: PairModel) {
        db.withTransaction {
            dao.deletePairEntity(pair.info.id)
        }
    }

    override suspend fun renameSchedule(id: Long, scheduleName: String) {
        val entity = dao.getScheduleEntity(id).firstOrNull()
        db.withTransaction {
            if (entity != null) {
                val newEntity = entity.copy(scheduleName = scheduleName)
                dao.updateScheduleItem(newEntity)
            }
        }
    }
}