package com.overklassniy.stankinschedule.news.core.data.repository

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Элемент RSS-ленты, полученный после парсинга.
 *
 * @property title Заголовок новости.
 * @property link Ссылка на полную новость.
 * @property description HTML-описание (превью) новости.
 * @property imageUrl URL изображения из тега enclosure.
 * @property category Категория новости (например, "Главные новости/").
 * @property pubDate Дата публикации в формате ISO yyyy-MM-dd.
 * @property rawPubDate Исходная дата из RSS для дополнительной обработки.
 */
data class RssItem(
    val title: String,
    val link: String,
    val description: String,
    val imageUrl: String?,
    val category: String?,
    val pubDate: String,
    val rawPubDate: String,
)

/**
 * Парсер RSS-лент с сайта stankin.ru.
 *
 * Использует Android [XmlPullParser] для извлечения элементов из RSS XML.
 * Поддерживает стандартные теги RSS 2.0: item, title, link, description,
 * enclosure, category, pubDate.
 */
object RssParser {

    /**
     * Формат даты RFC 2822, используемый в RSS pubDate.
     * Пример: "Thu, 05 Feb 2026 00:00:00 +0300"
     */
    private val rfc2822Format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply {
        timeZone = TimeZone.getTimeZone("Europe/Moscow")
    }

    /**
     * Парсит XML-строку RSS-ленты и возвращает список [RssItem].
     *
     * @param xml Сырая XML-строка RSS-ленты.
     * @return Список распарсенных элементов.
     */
    fun parse(xml: String): List<RssItem> {
        val items = mutableListOf<RssItem>()
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xml))

        var item: ItemBuilder? = null
        var currentTag = ""

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            when (parser.eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = parser.name
                    when (currentTag) {
                        "item" -> item = ItemBuilder()
                        "enclosure" -> item?.imageUrl = parser.getAttributeValue(null, "url")
                    }
                }

                XmlPullParser.TEXT -> {
                    item?.appendText(currentTag, parser.text?.trim() ?: "")
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "item") {
                        item?.let { items.add(it.build()) }
                        item = null
                    }
                    currentTag = ""
                }
            }
        }
        return items
    }

    /**
     * Накопитель полей элемента RSS во время обхода XML-тегов.
     */
    private class ItemBuilder {
        var title = ""
        var link = ""
        var description = ""
        var imageUrl: String? = null
        var category: String? = null
        var pubDate = ""

        /**
         * Добавляет текстовое содержимое текущего тега к соответствующему полю.
         */
        fun appendText(tag: String, text: String) {
            when (tag) {
                "title" -> title += text
                "link" -> link += text
                "description" -> description += text
                "category" -> category = (category ?: "") + text
                "pubDate" -> pubDate += text
            }
        }

        /**
         * Собирает накопленные поля в [RssItem].
         */
        fun build() = RssItem(
            title = title.trim(),
            link = link.trim(),
            description = description.trim(),
            imageUrl = imageUrl?.trim(),
            category = category?.trim(),
            pubDate = convertDate(pubDate.trim()),
            rawPubDate = pubDate.trim(),
        )
    }

    /**
     * Конвертирует дату из формата RFC 2822 в ISO формат yyyy-MM-dd.
     *
     * @param rfc2822 Строка даты в формате RFC 2822.
     * @return Дата в формате yyyy-MM-dd или текущая дата при ошибке.
     */
    private fun convertDate(rfc2822: String): String {
        return try {
            val date = rfc2822Format.parse(rfc2822) ?: return rfc2822
            isoFormat.format(date)
        } catch (_: Exception) {
            rfc2822
        }
    }

    /**
     * Фильтрует список RSS-элементов по категории.
     *
     * @param items Список элементов для фильтрации.
     * @param categoryFilter Строка для поиска в поле category (без учёта регистра).
     * @return Отфильтрованный список.
     */
    fun filterByCategory(items: List<RssItem>, categoryFilter: String): List<RssItem> {
        return items.filter { it.category?.contains(categoryFilter, ignoreCase = true) == true }
    }
}
