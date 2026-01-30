package com.overklassniy.stankinschedule.schedule.viewer.ui.components

/**
 * Состояние процесса переименования расписания.
 *
 * Используется для управления показом диалога и сообщений об ошибках.
 *
 * - Rename — диалог активен, доступен ввод
 * - Error — произошла ошибка при переименовании
 * - AlreadyExist — расписание с таким именем уже существует
 * - Success — переименование завершено успешно
 */
sealed interface RenameState {
    /** Диалог открыт, пользователь вводит новое имя. */
    object Rename : RenameState

    /** Ошибка переименования.
     * @property error Причина ошибки. Поле может не использоваться напрямую в UI.
     */
    class Error(@Suppress("unused") val error: Throwable) : RenameState

    /** Конфликт имен: расписание с таким именем уже существует. */
    class AlreadyExist : RenameState

    /** Успешное завершение операции переименования. */
    object Success : RenameState
}