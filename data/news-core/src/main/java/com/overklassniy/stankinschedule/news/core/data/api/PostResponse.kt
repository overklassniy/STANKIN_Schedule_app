package com.overklassniy.stankinschedule.news.core.data.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

/**
 * Класс данных, представляющий ответ API детальной информации о посте (новости).
 *
 * @param success Флаг успешности выполнения запроса.
 * @param data Объект [NewsPost], содержащий детальную информацию о новости.
 * @param error Сообщение об ошибке, если запрос не был успешен.
 */
class PostResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: NewsPost,
    @SerializedName("error") val error: String,
) {

    /**
     * Класс данных, описывающий структуру поста новости.
     *
     * @param id Уникальный идентификатор новости.
     * @param datetime Дата и время публикации (строка).
     * @param title Заголовок новости.
     * @param logo URL логотипа новости.
     * @param text Текстовое содержание новости (может быть пустым, если используется delta).
     * @param delta Форматированный контент новости (обычно JSON-структура Quill Delta).
     */
    class NewsPost(
        val id: Int,
        val datetime: String,
        val title: String,
        val logo: String,
        val text: String,
        val delta: String,
    )

    /**
     * Кастомный десериализатор Gson для преобразования JSON в объект [NewsPost].
     * Необходим для обработки сложной структуры JSON, где поля могут быть вложены или отсутствовать.
     */
    class NewsPostDeserializer : JsonDeserializer<NewsPost> {

        /**
         * Выполняет десериализацию JSON элемента в объект [NewsPost].
         *
         * Алгоритм:
         * 1. Проверяет, что входной JSON является объектом.
         * 2. Извлекает обязательные поля (id, date, title, logo).
         * 3. Безопасно извлекает поле text (обрабатывает null и исключения).
         * 4. Проверяет наличие обязательных полей, выбрасывает исключение при их отсутствии.
         * 5. Ищет поле "ops" (операции Quill Delta) рекурсивно через метод [find].
         * 6. Обрабатывает полученную строку delta: заменяет относительные пути к изображениям (/uploads...) на абсолютные URL.
         * 7. Создает и возвращает объект [NewsPost].
         *
         * @param json Входящий JSON элемент.
         * @param typeOfT Тип целевого объекта.
         * @param context Контекст десериализации.
         * @return Сформированный объект [NewsPost].
         * @throws JsonParseException Если JSON невалиден или отсутствуют обязательные поля.
         */
        override fun deserialize(
            json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?,
        ): NewsPost {
            val rootObject =
                json?.asJsonObject ?: throw JsonParseException("Response is not JSON object")

            val id = rootObject.get("id")?.asInt
            val date = rootObject.get("date")?.asString
            val title = rootObject.get("title")?.asString
            val logo = rootObject.get("logo")?.asString

            var text = ""
            try {
                val textObject = rootObject.get("text")
                if (textObject != null && !textObject.isJsonNull) {
                    text = rootObject.get("text").asString
                }
            } catch (_: Exception) {
                // Игнорируем ошибки при получении текста, так как он может отсутствовать
            }

            if (id == null || date == null || title == null || logo == null) {
                throw JsonParseException("JSON object has empty attributes")
            }

            // Поиск контента Delta и постобработка путей к файлам
            val delta = (find(rootObject, "ops")?.toString() ?: "")
                // замена относительных путей файлов на абсолютные
                .replace(Regex("(/uploads.+?)\"")) { result: MatchResult ->
                    StankinDeanNewsAPI.BASE_URL + result.value
                }

            return NewsPost(
                id,
                date,
                title,
                logo,
                text,
                delta
            )
        }

        /**
         * Рекурсивно ищет ключ в JSON объекте.
         *
         * Алгоритм:
         * 1. Проверяет наличие ключа [key] в текущем объекте [root]. Если есть - возвращает значение.
         * 2. Проверяет наличие вложенного объекта по ключу [treeKey].
         * 3. Если вложенный объект есть, рекурсивно вызывает себя для него.
         * 4. Если ключ не найден, возвращает null.
         *
         * @param root Корневой JSON объект для поиска.
         * @param key Искомый ключ (например, "ops").
         * @param treeKey Ключ вложенного объекта для обхода (по умолчанию "delta").
         * @return Найденный JSON элемент или null.
         */
        private fun find(root: JsonObject, key: String, treeKey: String = "delta"): JsonElement? {
            if (root.has(key)) {
                return root.get(key)
            }
            if (root.has(treeKey)) {
                return find(root.get(treeKey).asJsonObject, key, treeKey)
            }
            return null
        }
    }
}