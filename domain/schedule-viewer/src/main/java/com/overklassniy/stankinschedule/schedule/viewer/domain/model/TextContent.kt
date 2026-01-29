package com.overklassniy.stankinschedule.schedule.viewer.domain.model

/**
 * Простой текстовый контент для отображения.
 *
 * @property content Текст.
 */
class TextContent(val content: String) : ViewContent {
    /**
     * Проверяет, пустой ли контент.
     *
     * @return true, если [content] пуст, иначе false.
     */
    override fun isEmpty(): Boolean = content.isEmpty()
}