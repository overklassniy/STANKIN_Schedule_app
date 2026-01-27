package com.overklassniy.stankinschedule.schedule.core.data.mapper

import com.overklassniy.stankinschedule.schedule.core.data.api.PairJson
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateItem
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateRange
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateSingle
import com.overklassniy.stankinschedule.schedule.core.domain.model.Frequency
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.model.Time
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type

/**
 * Преобразует [PairJson] (модель из API) в доменную модель [PairModel].
 *
 * @return Доменная модель пары.
 */
fun PairJson.toPairModel(): PairModel {
    return PairModel(
        title = title,
        lecturer = lecturer,
        classroom = classroom,
        type = Type.of(type),
        subgroup = Subgroup.of(subgroup),
        time = Time(time.start, time.end),
        date = DateModel().apply { date.forEach { add(it.toDateItem()) } }
    )
}

/**
 * Преобразует [PairJson.DateJson] (модель даты из API) в доменную модель [DateItem].
 *
 * @return Доменная модель элемента даты.
 */
fun PairJson.DateJson.toDateItem(): DateItem {
    val f = Frequency.of(frequency)
    if (f == Frequency.ONCE) {
        return DateSingle(date)
    }
    return DateRange(date, f)
}

/**
 * Преобразует доменную модель [DateModel] в список [PairJson.DateJson] для отправки в API.
 *
 * @return Список моделей дат для API.
 */
fun DateModel.toJson(): List<PairJson.DateJson> {
    return this.map { date ->
        PairJson.DateJson(date.frequency().tag, date.toString())
    }
}

/**
 * Преобразует доменную модель [PairModel] в [PairJson] для отправки в API.
 *
 * @return Модель пары для API.
 */
fun PairModel.toJson() : PairJson {
    return PairJson(
        title = title,
        lecturer = lecturer,
        classroom = classroom,
        type = type.tag,
        subgroup = subgroup.tag,
        time = PairJson.TimeJson(time.startString(), time.endString()),
        date = date.toJson()
    )
}

/**
 * Преобразует доменную модель [ScheduleModel] в список [PairJson] для отправки в API.
 *
 * @return Список пар для API.
 */
fun ScheduleModel.toJson() : List<PairJson> {
    return this.map { pair -> pair.toJson() }
}