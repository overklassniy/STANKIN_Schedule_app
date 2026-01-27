package com.overklassniy.stankinschedule.core.data.mapper

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.joda.time.DateTime
import java.lang.reflect.Type

/**
 * Конвертер типов для сериализации и десериализации объектов [DateTime] библиотекой Gson.
 * Преобразует [DateTime] в строку (ISO 8601) и обратно.
 */
class DateTimeTypeConverter : JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

    /**
     * Сериализует объект [DateTime] в JSON элемент.
     *
     * Алгоритм:
     * 1. Преобразует [DateTime] в строковое представление.
     * 2. Оборачивает строку в [JsonPrimitive].
     *
     * @param src Объект [DateTime] для сериализации
     * @param typeOfSrc Тип исходного объекта
     * @param context Контекст сериализации
     * @return JSON элемент [JsonElement], содержащий строковое представление даты
     */
    override fun serialize(
        src: DateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?,
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }

    /**
     * Десериализует JSON элемент в объект [DateTime].
     *
     * Алгоритм:
     * 1. Получает строку из JSON элемента.
     * 2. Создает новый объект [DateTime] из строки.
     *
     * @param json JSON элемент для десериализации
     * @param type Тип объекта
     * @param context Контекст десериализации
     * @return Восстановленный объект даты и времени [DateTime]
     * @throws JsonParseException Если не удалось распарсить JSON
     */
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        type: Type?,
        context:
        JsonDeserializationContext?,
    ): DateTime {
        return DateTime(json.asString)
    }
}