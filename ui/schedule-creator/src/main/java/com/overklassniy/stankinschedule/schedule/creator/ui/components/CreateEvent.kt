package com.overklassniy.stankinschedule.schedule.creator.ui.components

/**
 * События создания расписания.
 *
 * Используется для управления диалогом создания и логикой ViewModel.
 */
sealed interface CreateEvent {

    /**
     * Отменяет создание расписания.
     */
    object Cancel : CreateEvent

    /**
     * Запрашивает создание нового пустого расписания.
     */
    object New : CreateEvent
}