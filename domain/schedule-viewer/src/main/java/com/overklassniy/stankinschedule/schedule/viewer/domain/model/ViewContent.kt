package com.overklassniy.stankinschedule.schedule.viewer.domain.model

/**
 * Контент для отображения в расписании (текст, ссылки и т.п.).
 */
interface ViewContent {

    /**
     * Проверяет, пустой ли контент.
     *
     * @return true, если контент пуст, иначе false.
     */
    fun isEmpty(): Boolean
}

/**
 * Обратная проверка пустоты контента.
 *
 * @return true, если контент не пуст.
 */
fun ViewContent.isNotEmpty() = !this.isEmpty()