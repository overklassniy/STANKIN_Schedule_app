package com.overklassniy.stankinschedule.schedule.core.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Сущность пары (занятия) для хранения в базе данных Room.
 * Связана с [ScheduleEntity] через внешний ключ [scheduleId].
 *
 * @property scheduleId ID расписания, к которому относится эта пара.
 * @property title Название предмета.
 * @property lecturer Имя преподавателя.
 * @property classroom Аудитория.
 * @property type Тип занятия (лекция, семинар, лабораторная и т.д.).
 * @property subgroup Подгруппа (например, "A", "B" или пустая строка).
 * @property time Время проведения занятия (строковое представление JSON объекта TimeJson).
 * @property date Информация о датах проведения (строковое представление JSON массива DateJson).
 * @property link Ссылка на занятие (необязательная).
 */
@Entity(
    tableName = "schedule_pair_entities",
    foreignKeys = [
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PairEntity(
    @ColumnInfo(name = "schedule_id", index = true)
    val scheduleId: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "lecturer")
    val lecturer: String,
    @ColumnInfo(name = "classroom")
    val classroom: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "subgroup")
    val subgroup: String,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "link", defaultValue = "")
    val link: String = "",
) {

    /**
     * Уникальный идентификатор пары в базе данных (автогенерация).
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}