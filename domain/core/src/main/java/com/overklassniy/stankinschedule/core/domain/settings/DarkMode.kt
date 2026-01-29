package com.overklassniy.stankinschedule.core.domain.settings

/**
 * Перечисление режимов темы оформления приложения.
 *
 * @property tag Строковый идентификатор режима для сохранения в настройках.
 */
enum class DarkMode(val tag: String) {
    /** Системная тема (по умолчанию). */
    Default("dark_mode_default"),

    /** Темная тема. */
    Dark("dark_mode_dark"),

    /** Светлая тема. */
    Light("dark_mode_light");

    companion object {
        /**
         * Получает экземпляр [DarkMode] по строковому идентификатору.
         *
         * @param value Строковый идентификатор (тег).
         * @return Соответствующий [DarkMode] или null, если не найдено.
         */
        fun from(value: String?): DarkMode? {
            return entries.find { it.tag == value }
        }
    }
}