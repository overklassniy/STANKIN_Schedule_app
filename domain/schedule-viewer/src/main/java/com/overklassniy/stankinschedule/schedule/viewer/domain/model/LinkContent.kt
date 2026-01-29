package com.overklassniy.stankinschedule.schedule.viewer.domain.model

/**
 * Контент с кликабельными ссылками для отображения в расписании.
 *
 * @property name Текстовое имя/заголовок блока ссылок.
 * @property links Список ссылок с позициями в тексте.
 */
class LinkContent(
    val name: String,
    val links: List<Link>
) : ViewContent {

    /**
     * Проверяет, пустой ли контент.
     *
     * @return true, если [name] пуст, иначе false.
     */
    override fun isEmpty(): Boolean = name.isEmpty()

    /**
     * Описание ссылки внутри текста.
     *
     * @property position Начальная позиция ссылки в тексте.
     * @property lenght Длина текста ссылки.
     * @property url URL-адрес.
     */
    class Link(
        val position: Int,
        val lenght: Int,
        val url: String
    )
}