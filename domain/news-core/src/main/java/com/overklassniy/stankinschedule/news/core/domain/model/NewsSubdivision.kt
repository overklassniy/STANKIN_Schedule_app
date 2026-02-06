package com.overklassniy.stankinschedule.news.core.domain.model

/**
 * Подразделения новостей университета.
 *
 * @property id Уникальный идентификатор подразделения, используемый для хранения в БД.
 */
enum class NewsSubdivision(val id: Int) {
    /** Главные новости университета (из RSS NEWS, категория "Главные новости"). */
    University(0),

    /** Международная деятельность (из RSS EXCHANGE). */
    Exchange(1),

    /** Аспирантура (из RSS NEWS, категория "Аспирантура"). */
    PhD(2),

    /** Новости деканата (из RSS NEWS, категория "Деканат"). */
    Dean(125),

    /** Анонсы (из RSS ADS). */
    Announcements(999);
}