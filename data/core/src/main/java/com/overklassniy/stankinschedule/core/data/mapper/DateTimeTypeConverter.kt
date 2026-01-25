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
 * Конвертор для Gson класса DateTime.
 */
class DateTimeTypeConverter : JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

    override fun serialize(
        src: DateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?,
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }

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
