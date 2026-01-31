package com.overklassniy.stankinschedule.news.core.data.mapper

import com.overklassniy.stankinschedule.news.core.data.api.PostResponse
import com.overklassniy.stankinschedule.news.core.data.api.StankinDeanNewsAPI
import com.overklassniy.stankinschedule.news.core.data.db.NewsEntity
import com.overklassniy.stankinschedule.news.core.domain.model.NewsContent
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost

/**
 * Преобразует модель новости доменного слоя в сущность базы данных.
 * Используется для сохранения загруженных новостей в кэш (БД).
 *
 * @param index Порядковый номер новости в списке (для сохранения порядка сортировки).
 * @param newsSubdivision ID подразделения, к которому относится новость.
 * @return Сущность [NewsEntity] для вставки в БД.
 */
fun NewsPost.toEntity(
    index: Int,
    newsSubdivision: Int,
): NewsEntity {
    return NewsEntity(
        id = id,
        indexOrder = index,
        newsSubdivision = newsSubdivision,
        title = title,
        date = date,
        logo = previewImageUrl ?: "",
        relativeUrl = relativeUrl
    )
}

/**
 * Преобразует модель новости из ответа API деканата в доменную модель контента новости.
 * Используется при загрузке полного текста новости.
 *
 * @return Доменная модель [NewsContent], содержащая текст и детали новости.
 */
fun PostResponse.NewsPost.toNewsContent(): NewsContent {
    return NewsContent(
        id = id,
        // Извлекаем только дату из строки datetime (отсекаем время)
        date = datetime.split(" ").first(),
        title = title,
        // Формируем полный URL изображения, добавляя базовый URL API
        previewImageUrl = StankinDeanNewsAPI.BASE_URL + logo,
        text = text,
        deltaFormat = delta
    )
}

/**
 * Преобразует сущность базы данных в доменную модель новости.
 * Используется при чтении новостей из кэша (БД).
 *
 * @return Доменная модель [NewsPost] для отображения в списке.
 */
fun NewsEntity.toPost(): NewsPost {
    return NewsPost(
        id = id,
        title = title,
        // Пустая строка означает отсутствие изображения
        previewImageUrl = logo.ifEmpty { null },
        date = date,
        relativeUrl = relativeUrl
    )
}