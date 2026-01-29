package com.overklassniy.stankinschedule.news.core.domain.model

/**
 * Краткая модель новости (для списка).
 *
 * @property id Уникальный идентификатор новости.
 * @property title Заголовок новости.
 * @property previewImageUrl Ссылка на изображение для превью.
 * @property date Дата публикации.
 * @property relativeUrl Относительный URL новости (может использоваться для deep links или web view).
 */
data class NewsPost(
    val id: Int,
    val title: String,
    val previewImageUrl: String?,
    val date: String,
    val relativeUrl: String?
)