/**
 * Состояние диалога создания расписания.
 *
 * Описывает новые вводы, ошибки, конфликт имени и успешное создание.
 */
package com.overklassniy.stankinschedule.schedule.creator.ui.components

/**
 * Контракт состояний создания расписания.
 */
sealed interface CreateState {

    /**
     * Состояние ввода нового имени расписания.
     */
    object New : CreateState

    /**
     * Ошибка создания расписания.
     *
     * @property error Исключение (не используется в UI напрямую).
     */
    class Error(@Suppress("Unused") val error: Throwable) : CreateState

    /**
     * Конфликт имени: такое расписание уже существует.
     */
    class AlreadyExist : CreateState

    /**
     * Успешное создание расписания.
     */
    object Success : CreateState
}