package com.overklassniy.stankinschedule.journal.core.data.mapper

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.overklassniy.stankinschedule.journal.core.domain.model.MarkType
import java.lang.reflect.Type

/**
 * Конвертер типов для сериализации и десериализации [MarkType] с использованием библиотеки Gson.
 * Позволяет преобразовывать объекты типа [MarkType] в строковое представление JSON и обратно.
 */
class MarkTypeTypeConverter : JsonSerializer<MarkType>, JsonDeserializer<MarkType> {

    /**
     * Сериализует объект [MarkType] в JSON элемент.
     *
     * Алгоритм:
     * 1. Преобразует объект [MarkType] в его строковое представление.
     * 2. Оборачивает строку в [JsonPrimitive].
     *
     * @param src Объект [MarkType] для сериализации
     * @param typeOfSrc Тип исходного объекта (не используется в данной реализации)
     * @param context Контекст сериализации (не используется в данной реализации)
     * @return JSON элемент [JsonElement], содержащий строковое представление типа оценки
     */
    override fun serialize(
        src: MarkType?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?,
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }

    /**
     * Десериализует JSON элемент в объект [MarkType].
     *
     * Алгоритм:
     * 1. Получает строковое значение из JSON элемента.
     * 2. Использует фабричный метод [MarkType.of] для получения соответствующего экземпляра enum.
     *
     * @param json JSON элемент для десериализации
     * @param typeOfT Тип целевого объекта (не используется в данной реализации)
     * @param context Контекст десериализации (не используется в данной реализации)
     * @return Объект [MarkType], соответствующий строке из JSON
     * @throws JsonParseException Если возникла ошибка при разборе JSON (например, неизвестный тип оценки)
     */
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): MarkType {
        return MarkType.of(json.asString)
    }
}