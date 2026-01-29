package com.overklassniy.stankinschedule.core.domain.settings

/**
 * Перечисление доступных языков приложения.
 *
 * @param tag Тег языка для сохранения в настройках
 * @param localeCode Код локали (пустая строка означает системный язык)
 */
enum class AppLanguage(val tag: String, val localeCode: String) {
    /** Системный язык по умолчанию */
    System("app_language_system", ""),

    /** Русский язык */
    Russian("app_language_russian", "ru"),

    /** Английский язык */
    English("app_language_english", "en");

    companion object {
        /**
         * Получает язык по тегу.
         *
         * @param value Тег языка
         * @return Соответствующий язык или null, если не найден
         */
        fun from(value: String?): AppLanguage? {
            return entries.find { it.tag == value }
        }
    }
}
