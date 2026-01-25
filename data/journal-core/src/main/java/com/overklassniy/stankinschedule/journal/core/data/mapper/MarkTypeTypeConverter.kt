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

class MarkTypeTypeConverter : JsonSerializer<MarkType>, JsonDeserializer<MarkType> {

    override fun serialize(
        src: MarkType?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?,
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): MarkType {
        return MarkType.of(json.asString)
    }
}