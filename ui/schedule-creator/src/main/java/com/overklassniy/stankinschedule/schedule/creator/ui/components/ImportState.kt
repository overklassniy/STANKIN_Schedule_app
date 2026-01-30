/**
 * Состояние импорта расписания с устройства.
 *
 * Описывает успешный результат с именем расписания и ошибку импорта.
 */
package com.overklassniy.stankinschedule.schedule.creator.ui.components

/**
 * Контракт состояний импорта.
 */
sealed interface ImportState {

    /**
     * Ошибка импорта.
     *
     * @property error Исключение импорта (не используется в UI напрямую).
     */
    class Failed(@Suppress("Unused") val error: Throwable) : ImportState

    /**
     * Успешный импорт.
     *
     * @property scheduleName Имя добавленного расписания.
     */
    class Success(val scheduleName: String) : ImportState
}