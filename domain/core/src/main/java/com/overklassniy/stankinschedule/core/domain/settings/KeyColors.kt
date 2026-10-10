package com.overklassniy.stankinschedule.core.domain.settings

/**
 * Набор из 6 ключевых цветов темы, из которых автоматически вычисляются остальные 22 цвета Material 3.
 *
 * Пользователь настраивает только эти 6 цветов. Остальные цвета (on*, *Container,
 * outline, inverse*, surfaceVariant, surfaceTint) вычисляются через [ThemeColors.derive].
 *
 * @property primary Основной цвет.
 * @property secondary Вторичный цвет.
 * @property tertiary Третичный цвет.
 * @property background Цвет фона.
 * @property surface Цвет поверхностей.
 * @property error Цвет ошибок.
 */
data class KeyColors(
    val primary: String,
    val secondary: String,
    val tertiary: String,
    val background: String,
    val surface: String,
    val error: String,
) {
    companion object {
        /**
         * Возвращает ключевые цвета встроенной светлой палитры.
         *
         * @return 6 ключевых цветов светлой темы.
         */
        fun lightDefault() = KeyColors(
            primary = "#4355B9",
            secondary = "#5B5D72",
            tertiary = "#77536D",
            background = "#FEFBFF",
            surface = "#FEFBFF",
            error = "#BA1A1A",
        )

        /**
         * Возвращает ключевые цвета встроенной тёмной палитры.
         *
         * @return 6 ключевых цветов тёмной темы.
         */
        fun darkDefault() = KeyColors(
            primary = "#BAC3FF",
            secondary = "#C3C5DD",
            tertiary = "#E6BAD7",
            background = "#1B1B1F",
            surface = "#1B1B1F",
            error = "#FFB4AB",
        )
    }
}
