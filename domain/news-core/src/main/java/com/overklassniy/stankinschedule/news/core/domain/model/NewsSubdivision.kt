package com.overklassniy.stankinschedule.news.core.domain.model

/**
 * Подразделения новостей университета.
 *
 * @property id Уникальный идентификатор подразделения, используемый в API.
 */
enum class NewsSubdivision(val id: Int) {
    /** Новости университета. */
    University(0),

    /** Новости деканата. */
    Dean(125),

    /** Объявления. */
    Announcements(999);
}