package com.overklassniy.stankinschedule.schedule.core.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.overklassniy.stankinschedule.schedule.core.data.mapper.DateTimeConverter
import org.joda.time.DateTime

/**
 * Сущность расписания для хранения в базе данных Room.
 * Представляет собой заголовок расписания (например, группу).
 *
 * @property scheduleName Имя расписания (уникальное поле, например "ИДБ-20-01").
 */
@Entity(
    tableName = "schedule_entities",
    indices = [
        Index("schedule_name", unique = true)
    ]
)
@TypeConverters(DateTimeConverter::class)
class ScheduleEntity(
    @ColumnInfo(name = "schedule_name")
    var scheduleName: String,
) {

    /**
     * Уникальный идентификатор расписания в БД (автогенерация).
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * Дата и время последнего обновления расписания.
     */
    @ColumnInfo(name = "last_update")
    var lastUpdate: DateTime? = null

    /**
     * Флаг синхронизации с сервером.
     * true, если расписание успешно синхронизировано.
     */
    @ColumnInfo(name = "synced")
    var synced: Boolean = false

    /**
     * Позиция расписания в списке (для пользовательской сортировки).
     */
    @ColumnInfo(name = "position")
    var position: Int = 0

    /**
     * Создает копию объекта с возможностью изменения отдельных полей.
     *
     * @param scheduleName Новое имя расписания.
     * @param id Новый ID.
     * @param lastUpdate Новое время обновления.
     * @param synced Новый статус синхронизации.
     * @param position Новая позиция.
     * @return Новый объект [ScheduleEntity].
     */
    fun copy(
        scheduleName: String = this.scheduleName,
        id: Long = this.id,
        lastUpdate: DateTime? = this.lastUpdate,
        synced: Boolean = this.synced,
        position: Int = this.position
    ): ScheduleEntity = ScheduleEntity(scheduleName).apply {
        this.id = id
        this.lastUpdate = lastUpdate
        this.synced = synced
        this.position = position
    }
}