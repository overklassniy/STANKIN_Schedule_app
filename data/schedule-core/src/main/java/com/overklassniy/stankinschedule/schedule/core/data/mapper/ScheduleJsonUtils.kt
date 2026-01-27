package com.overklassniy.stankinschedule.schedule.core.data.mapper

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateItem
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateRange
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateSingle
import com.overklassniy.stankinschedule.schedule.core.domain.model.Frequency

/**
 * Утилиты для работы с JSON представлением расписания.
 */
object ScheduleJsonUtils {

    /**
     * Преобразует [DateModel] в [JsonElement].
     *
     * @param date Модель даты.
     * @return JSON элемент, представляющий дату.
     */
    fun toJson(date: DateModel): JsonElement {
        val jsonDate = JsonArray()
        for (item in date) {
            val jsonItem: JsonElement = when (item) {
                is DateSingle -> {
                    JsonObject().apply {
                        addProperty(
                            DateItem.JSON_DATE,
                            item.toString(DateItem.JSON_DATE_PATTERN_V2)
                        )
                        addProperty(
                            DateItem.JSON_FREQUENCY,
                            item.frequency().tag
                        )
                    }
                }
                is DateRange -> {
                    JsonObject().apply {
                        addProperty(
                            DateItem.JSON_DATE,
                            item.start.toString(DateItem.JSON_DATE_PATTERN_V2) +
                                    DateItem.JSON_DATE_SEP +
                                    item.end.toString(DateItem.JSON_DATE_PATTERN_V2)
                        )
                        addProperty(
                            DateItem.JSON_FREQUENCY,
                            item.frequency().tag
                        )
                    }
                }
            }
            jsonDate.add(jsonItem)
        }

        return jsonDate
    }

    /**
     * Преобразует [JsonElement] в [DateModel].
     *
     * @param jsonElement JSON элемент с данными о дате.
     * @return Доменная модель даты.
     */
    fun dateFromJson(jsonElement: JsonElement): DateModel {
        val date = DateModel()
        val dateArray = jsonElement.asJsonArray

        for (jsonDate in dateArray) {
            val json = jsonDate.asJsonObject
            val frequency = Frequency.of(json[DateItem.JSON_FREQUENCY].asString)

            if (frequency == Frequency.ONCE) {
                date.add(DateSingle(json[DateItem.JSON_DATE].asString))
            } else {
                date.add(DateRange(json[DateItem.JSON_DATE].asString, frequency))
            }
        }
        return date
    }
}