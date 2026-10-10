package com.overklassniy.stankinschedule.core.domain.settings

/**
 * Перечисление пресетов темы оформления приложения.
 *
 * Пресет определяет, какая цветовая схема Material 3 используется:
 * встроенная (светлая, тёмная, системная) или кастомная (с ручной настройкой цветов).
 *
 * @property tag Строковый идентификатор пресета для сохранения в настройках.
 */
enum class ThemePreset(val tag: String) {
    /** Системная тема — следует системной настройке тёмной/светлой темы. */
    System("theme_system"),

    /** Светлая тема — встроенный пресет. */
    Light("theme_light"),

    /** Тёмная тема — встроенный пресет. */
    Dark("theme_dark"),

    /** Кастомная тема — автоматически устанавливается при ручном изменении любого цвета. */
    Custom("theme_custom");

    companion object {
        /**
         * Получает экземпляр [ThemePreset] по строковому идентификатору.
         *
         * @param value Строковый идентификатор (тег).
         * @return Соответствующий [ThemePreset] или null, если не найдено.
         */
        fun from(value: String?): ThemePreset? {
            return entries.find { it.tag == value }
        }
    }
}
