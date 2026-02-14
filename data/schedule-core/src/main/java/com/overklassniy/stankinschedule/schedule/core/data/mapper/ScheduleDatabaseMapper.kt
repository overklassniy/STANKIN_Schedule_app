package com.overklassniy.stankinschedule.schedule.core.data.mapper

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.overklassniy.stankinschedule.schedule.core.data.db.PairEntity
import com.overklassniy.stankinschedule.schedule.core.data.db.ScheduleEntity
import com.overklassniy.stankinschedule.schedule.core.data.db.ScheduleWithPairs
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairInfo
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.model.Time
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type

/**
 * Преобразует [ScheduleWithPairs] (расписание с парами из БД) в доменную модель [ScheduleModel].
 *
 * @return Доменная модель расписания.
 */
fun ScheduleWithPairs.toScheduleModel(): ScheduleModel {
    val info = ScheduleInfo(
        id = schedule.id,
        scheduleName = schedule.scheduleName,
        lastUpdate = schedule.lastUpdate,
        position = schedule.position,
        synced = schedule.synced
    )

    val model = ScheduleModel(info)
    for (pair in pairs) {
        model.add(pair.toPairModel())
    }

    return model
}

/**
 * Преобразует [ScheduleEntity] в информацию о расписании [ScheduleInfo].
 *
 * @return Информация о расписании.
 */
fun ScheduleEntity.toInfo(): ScheduleInfo {
    return ScheduleInfo(
        id = id,
        scheduleName = scheduleName,
        lastUpdate = lastUpdate,
        position = position,
        synced = synced
    )
}

/**
 * Преобразует информацию о расписании [ScheduleInfo] в сущность БД [ScheduleEntity].
 *
 * @return Сущность расписания для БД.
 */
fun ScheduleInfo.toEntity(): ScheduleEntity {
    return ScheduleEntity(scheduleName = scheduleName).apply {
        this.id = this@toEntity.id
        this.lastUpdate = this@toEntity.lastUpdate
        this.position = this@toEntity.position
        this.synced = this@toEntity.synced
    }
}

/**
 * Преобразует [PairEntity] в доменную модель пары [PairModel].
 *
 * @return Доменная модель пары.
 */
fun PairEntity.toPairModel(): PairModel {
    return PairModel(
        title = title,
        lecturer = lecturer,
        classroom = classroom,
        type = Type.of(type),
        subgroup = Subgroup.of(subgroup),
        time = Time.fromString(time),
        date = ScheduleJsonUtils.dateFromJson(
            Gson().fromJson(date, JsonElement::class.java)
        ),
        link = link,
        info = PairInfo(
            scheduleId = scheduleId,
            id = id
        )
    )
}

/**
 * Преобразует доменную модель расписания [ScheduleModel] в сущность БД [ScheduleEntity].
 *
 * @param position Позиция расписания в списке (опционально). Если null, берется из info.
 * @return Сущность расписания для БД.
 */
fun ScheduleModel.toEntity(position: Int? = null): ScheduleEntity {
    return ScheduleEntity(info.scheduleName).apply {
        this.id = info.id
        this.lastUpdate = info.lastUpdate
        this.position = position ?: info.position
        this.synced = info.synced
    }
}

/**
 * Преобразует доменную модель пары [PairModel] в сущность БД [PairEntity].
 *
 * @param scheduleId Идентификатор расписания, к которому относится пара.
 * @return Сущность пары для БД.
 */
fun PairModel.toEntity(scheduleId: Long): PairEntity {
    return PairEntity(
        scheduleId = scheduleId,
        title = title,
        lecturer = lecturer,
        classroom = classroom,
        type = type.tag,
        subgroup = subgroup.tag,
        time = time.toString(),
        date = ScheduleJsonUtils.toJson(date).toString(),
        link = link
    ).apply {
        this.id = info.id
    }
}