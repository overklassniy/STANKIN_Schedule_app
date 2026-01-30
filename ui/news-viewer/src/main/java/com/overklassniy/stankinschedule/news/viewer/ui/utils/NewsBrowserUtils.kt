package com.overklassniy.stankinschedule.news.viewer.ui.utils

/**
 * Утилиты для формирования ссылок на новости для открытия в браузере.
 */
object NewsBrowserUtils {
    /**
     * Формирует абсолютную ссылку на страницу новости по её идентификатору.
     *
     * @param newsId Идентификатор новости.
     * @return Абсолютный URL страницы новости.
     */
    fun linkForPost(newsId: Int) = "https://stankin.ru/news/item_${newsId}"
}