package com.overklassniy.stankinschedule.schedule.viewer.ui.components

/**
 * События диалога переименования расписания.
 *
 * - Rename — открыть диалог и инициировать ввод нового имени
 * - Cancel — закрыть диалог без изменений
 */
sealed interface RenameEvent {
    /** Открыть диалог переименования. */
    object Rename : RenameEvent

    /** Закрыть диалог без переименования. */
    object Cancel : RenameEvent
}