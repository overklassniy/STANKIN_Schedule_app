package com.overklassniy.stankinschedule.schedule.editor.ui.components

import com.overklassniy.stankinschedule.schedule.core.domain.model.DateItem

/**
 * Запрос на редактирование даты пары.
 *
 * Используется для открытия диалога выбора даты.
 */
sealed interface DateEditorRequest {
    /** Редактирование существующего элемента даты. */
    class Edit(val date: DateItem) : DateEditorRequest

    /** Создание нового элемента даты. */
    object New : DateEditorRequest
}