package com.overklassniy.stankinschedule.core.domain.settings

/**
 * Цветовая схема Material 3 в HEX-формате.
 *
 * Содержит все 28 цветов Material 3 для построения ColorScheme из androidx.compose.material3.
 * Значения хранятся в формате `#RRGGBB` или `#AARRGGBB`.
 *
 * Для встроенных пресетов используются значения из `lightDefault()` и `darkDefault()`,
 * которые соответствуют палитрам из `Color.kt`.
 *
 * @property primary Основной цвет.
 * @property onPrimary Цвет текста/иконок поверх [primary].
 * @property primaryContainer Контейнер основного цвета.
 * @property onPrimaryContainer Цвет текста/иконок поверх [primaryContainer].
 * @property secondary Вторичный цвет.
 * @property onSecondary Цвет текста/иконок поверх [secondary].
 * @property secondaryContainer Контейнер вторичного цвета.
 * @property onSecondaryContainer Цвет текста/иконок поверх [secondaryContainer].
 * @property tertiary Третичный цвет.
 * @property onTertiary Цвет текста/иконок поверх [tertiary].
 * @property tertiaryContainer Контейнер третичного цвета.
 * @property onTertiaryContainer Цвет текста/иконок поверх [tertiaryContainer].
 * @property error Цвет ошибок.
 * @property onError Цвет текста/иконок поверх [error].
 * @property errorContainer Контейнер цвета ошибок.
 * @property onErrorContainer Цвет текста/иконок поверх [errorContainer].
 * @property background Цвет фона.
 * @property onBackground Цвет текста/иконок поверх [background].
 * @property surface Цвет поверхностей.
 * @property onSurface Цвет текста/иконок поверх [surface].
 * @property surfaceVariant Вариант цвета поверхностей.
 * @property onSurfaceVariant Цвет текста/иконок поверх [surfaceVariant].
 * @property outline Цвет контуров и разделителей.
 * @property inverseSurface Цвет инвертированной поверхности.
 * @property inverseOnSurface Цвет текста/иконок поверх [inverseSurface].
 * @property inversePrimary Инвертированный основной цвет.
 * @property surfaceTint Цвет tint для поверхностей.
 */
data class ThemeColors(
    val primary: String,
    val onPrimary: String,
    val primaryContainer: String,
    val onPrimaryContainer: String,
    val secondary: String,
    val onSecondary: String,
    val secondaryContainer: String,
    val onSecondaryContainer: String,
    val tertiary: String,
    val onTertiary: String,
    val tertiaryContainer: String,
    val onTertiaryContainer: String,
    val error: String,
    val onError: String,
    val errorContainer: String,
    val onErrorContainer: String,
    val background: String,
    val onBackground: String,
    val surface: String,
    val onSurface: String,
    val surfaceVariant: String,
    val onSurfaceVariant: String,
    val outline: String,
    val inverseSurface: String,
    val inverseOnSurface: String,
    val inversePrimary: String,
    val surfaceTint: String,
) {
    companion object {
        /**
         * Возвращает встроенную светлую палитру Material 3.
         *
         * Значения соответствуют константам `md_theme_light_*` из `Color.kt`.
         *
         * @return Цветовая схема светлой темы.
         */
        fun lightDefault() = ThemeColors(
            primary = "#4355B9",
            onPrimary = "#FFFFFF",
            primaryContainer = "#DEE0FF",
            onPrimaryContainer = "#00105C",
            secondary = "#5B5D72",
            onSecondary = "#FFFFFF",
            secondaryContainer = "#E0E1F9",
            onSecondaryContainer = "#181A2C",
            tertiary = "#77536D",
            onTertiary = "#FFFFFF",
            tertiaryContainer = "#FFD7F1",
            onTertiaryContainer = "#2D1228",
            error = "#BA1A1A",
            onError = "#FFFFFF",
            errorContainer = "#FFDAD6",
            onErrorContainer = "#410002",
            background = "#FEFBFF",
            onBackground = "#1B1B1F",
            surface = "#FEFBFF",
            onSurface = "#1B1B1F",
            surfaceVariant = "#E3E1EC",
            onSurfaceVariant = "#46464F",
            outline = "#767680",
            inverseSurface = "#303034",
            inverseOnSurface = "#F3F0F4",
            inversePrimary = "#BAC3FF",
            surfaceTint = "#4355B9",
        )

        /**
         * Возвращает встроенную тёмную палитру Material 3.
         *
         * Значения соответствуют константам `md_theme_dark_*` из `Color.kt`.
         *
         * @return Цветовая схема тёмной темы.
         */
        fun darkDefault() = ThemeColors(
            primary = "#BAC3FF",
            onPrimary = "#08218A",
            primaryContainer = "#293CA0",
            onPrimaryContainer = "#DEE0FF",
            secondary = "#C3C5DD",
            onSecondary = "#2D2F42",
            secondaryContainer = "#434659",
            onSecondaryContainer = "#E0E1F9",
            tertiary = "#E6BAD7",
            onTertiary = "#44263D",
            tertiaryContainer = "#5D3C55",
            onTertiaryContainer = "#FFD7F1",
            error = "#FFB4AB",
            onError = "#690005",
            errorContainer = "#93000A",
            onErrorContainer = "#FFDAD6",
            background = "#1B1B1F",
            onBackground = "#E4E1E6",
            surface = "#1B1B1F",
            onSurface = "#E4E1E6",
            surfaceVariant = "#46464F",
            onSurfaceVariant = "#C7C5D0",
            outline = "#90909A",
            inverseSurface = "#E4E1E6",
            inverseOnSurface = "#1B1B1F",
            inversePrimary = "#4355B9",
            surfaceTint = "#BAC3FF",
        )

        /**
         * Вычисляет полную цветовую схему (28 цветов) из 6 ключевых цветов.
         *
         * Алгоритм:
         * 1. Определить режим (светлый/тёмный) по luminance фона.
         * 2. on* цвета вычисляются через контраст (белый/чёрный) по luminance базового цвета.
         * 3. Container цвета — осветление или затемнение базового цвета в зависимости от режима.
         * 4. onContainer — контрастный цвет к контейнеру.
         * 5. surfaceVariant — лёгкое затемнение/осветление surface.
         * 6. outline — смешение onSurface и surfaceVariant.
         * 7. inverse* — инверсия соответствующих цветов.
         * 8. surfaceTint — копия primary.
         *
         * @param keyColors 6 ключевых цветов.
         * @return Полная цветовая схема [ThemeColors] из 28 цветов.
         */
        fun derive(keyColors: KeyColors): ThemeColors {
            val isLight = luminance(keyColors.background) >= 0.5

            val onPrimary = contrastOn(keyColors.primary)
            val onSecondary = contrastOn(keyColors.secondary)
            val onTertiary = contrastOn(keyColors.tertiary)
            val onError = contrastOn(keyColors.error)
            val onBackground = contrastOn(keyColors.background)
            val onSurface = contrastOn(keyColors.surface)

            val primaryContainer = if (isLight) lighten(keyColors.primary, 0.7) else darken(keyColors.primary, 0.3)
            val onPrimaryContainer = contrastOn(primaryContainer)
            val secondaryContainer = if (isLight) lighten(keyColors.secondary, 0.7) else darken(keyColors.secondary, 0.3)
            val onSecondaryContainer = contrastOn(secondaryContainer)
            val tertiaryContainer = if (isLight) lighten(keyColors.tertiary, 0.7) else darken(keyColors.tertiary, 0.3)
            val onTertiaryContainer = contrastOn(tertiaryContainer)
            val errorContainer = if (isLight) lighten(keyColors.error, 0.7) else darken(keyColors.error, 0.3)
            val onErrorContainer = contrastOn(errorContainer)

            val surfaceVariant = if (isLight) darken(keyColors.surface, 0.1) else lighten(keyColors.surface, 0.1)
            val onSurfaceVariant = contrastOn(surfaceVariant)
            val outline = blend(onSurface, surfaceVariant)

            val inverseSurface = if (isLight) darken(keyColors.surface, 0.8) else lighten(keyColors.surface, 0.8)
            val inverseOnSurface = contrastOn(inverseSurface)
            val inversePrimary = if (isLight) lighten(keyColors.primary, 0.5) else darken(keyColors.primary, 0.5)

            return ThemeColors(
                primary = keyColors.primary,
                onPrimary = onPrimary,
                primaryContainer = primaryContainer,
                onPrimaryContainer = onPrimaryContainer,
                secondary = keyColors.secondary,
                onSecondary = onSecondary,
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = onSecondaryContainer,
                tertiary = keyColors.tertiary,
                onTertiary = onTertiary,
                tertiaryContainer = tertiaryContainer,
                onTertiaryContainer = onTertiaryContainer,
                error = keyColors.error,
                onError = onError,
                errorContainer = errorContainer,
                onErrorContainer = onErrorContainer,
                background = keyColors.background,
                onBackground = onBackground,
                surface = keyColors.surface,
                onSurface = onSurface,
                surfaceVariant = surfaceVariant,
                onSurfaceVariant = onSurfaceVariant,
                outline = outline,
                inverseSurface = inverseSurface,
                inverseOnSurface = inverseOnSurface,
                inversePrimary = inversePrimary,
                surfaceTint = keyColors.primary,
            )
        }

        // Вспомогательные функции для работы с HEX-цветами в чистом Kotlin

        /** Основание шестнадцатеричной системы для разбора компонентов цвета. */
        private const val HEX_RADIX = 16

        /** Максимальное значение цветового канала RGB. */
        private const val CHANNEL_MAX = 255

        /** Коэффициенты относительной яркости каналов по формуле W3C. */
        private const val LUMINANCE_R = 0.2126
        private const val LUMINANCE_G = 0.7152
        private const val LUMINANCE_B = 0.0722

        /** Порог яркости для выбора контрастного цвета текста. */
        private const val LUMINANCE_THRESHOLD = 0.5

        /** Границы компонентов в HEX-строке формата `#RRGGBB`. */
        private const val GREEN_START = 2
        private const val GREEN_END = 4
        private const val BLUE_END = 6

        /**
         * Разбирает HEX-строку в компоненты RGB.
         *
         * @param hex Строка в формате `#RRGGBB`.
         * @return Массив из трёх целых чисел [r, g, b] в диапазоне 0-255.
         */
        private fun parseHex(hex: String): IntArray {
            val clean = hex.removePrefix("#")
            return intArrayOf(
                clean.substring(0, GREEN_START).toInt(HEX_RADIX),
                clean.substring(GREEN_START, GREEN_END).toInt(HEX_RADIX),
                clean.substring(GREEN_END, BLUE_END).toInt(HEX_RADIX),
            )
        }

        /**
         * Преобразует компоненты RGB в HEX-строку.
         *
         * @param r Красный компонент (0-255).
         * @param g Зелёный компонент (0-255).
         * @param b Синий компонент (0-255).
         * @return Строка в формате `#RRGGBB`.
         */
        private fun toHex(r: Int, g: Int, b: Int): String =
            String.format(
                "#%02X%02X%02X",
                r.coerceIn(0, CHANNEL_MAX),
                g.coerceIn(0, CHANNEL_MAX),
                b.coerceIn(0, CHANNEL_MAX)
            )

        /**
         * Вычисляет относительную яркость (luminance) цвета по формуле W3C.
         *
         * @param hex HEX-строка цвета.
         * @return Значение luminance от 0.0 (чёрный) до 1.0 (белый).
         */
        private fun luminance(hex: String): Double {
            val rgb = parseHex(hex)
            return (LUMINANCE_R * rgb[0] + LUMINANCE_G * rgb[1] + LUMINANCE_B * rgb[2]) / CHANNEL_MAX
        }

        /**
         * Возвращает контрастный цвет (белый или чёрный) для обеспечения читаемости текста.
         *
         * @param hex Базовый HEX-цвет.
         * @return `#FFFFFF` если фон тёмный, `#000000` если светлый.
         */
        private fun contrastOn(hex: String): String =
            if (luminance(hex) < LUMINANCE_THRESHOLD) "#FFFFFF" else "#000000"

        /**
         * Осветляет цвет, смешивая его с белым.
         *
         * @param hex Исходный HEX-цвет.
         * @param amount Степень осветления от 0.0 (без изменений) до 1.0 (белый).
         * @return Осветлённый HEX-цвет.
         */
        private fun lighten(hex: String, amount: Double): String {
            val rgb = parseHex(hex)
            return toHex(
                (rgb[0] + (CHANNEL_MAX - rgb[0]) * amount).toInt(),
                (rgb[1] + (CHANNEL_MAX - rgb[1]) * amount).toInt(),
                (rgb[2] + (CHANNEL_MAX - rgb[2]) * amount).toInt(),
            )
        }

        /**
         * Затемняет цвет, смешивая его с чёрным.
         *
         * @param hex Исходный HEX-цвет.
         * @param amount Степень затемнения от 0.0 (без изменений) до 1.0 (чёрный).
         * @return Затемнённый HEX-цвет.
         */
        private fun darken(hex: String, amount: Double): String {
            val rgb = parseHex(hex)
            return toHex(
                (rgb[0] * (1 - amount)).toInt(),
                (rgb[1] * (1 - amount)).toInt(),
                (rgb[2] * (1 - amount)).toInt(),
            )
        }

        /**
         * Смешивает два цвета в равной пропорции (50/50).
         *
         * @param hex1 Первый HEX-цвет.
         * @param hex2 Второй HEX-цвет.
         * @return Смешанный HEX-цвет.
         */
        private fun blend(hex1: String, hex2: String): String {
            val rgb1 = parseHex(hex1)
            val rgb2 = parseHex(hex2)
            return toHex(
                ((rgb1[0] + rgb2[0]) / 2),
                ((rgb1[1] + rgb2[1]) / 2),
                ((rgb1[2] + rgb2[2]) / 2),
            )
        }
    }
}
