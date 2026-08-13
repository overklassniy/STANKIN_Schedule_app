package com.overklassniy.stankinschedule.news.widget.domain.model

import com.overklassniy.stankinschedule.core.domain.settings.KeyColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemeColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemePreset
import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision

/**
 * Данные для конфигурации виджета новостей.
 *
 * @property selectedSubdivisions Выбранные категории новостей для отображения в виджете.
 * @property themePreset Пресет темы виджета. Независим от темы приложения.
 * @property keyColors Ключевые цвета для пресета Custom. null для остальных пресетов.
 * @property textColor Переопределение цвета текста (onSurface и onBackground). null для автоматического вычисления.
 * @property announcementsColor Переопределение цвета метки категории «Анонсы». null для использования surfaceTint.
 */
data class NewsWidgetData(
    val selectedSubdivisions: Set<NewsSubdivision>,
    val themePreset: ThemePreset = ThemePreset.System,
    val keyColors: KeyColors? = null,
    val textColor: String? = null,
    val announcementsColor: String? = null,
) {

    /**
     * Вычисляет полную цветовую схему [ThemeColors] для виджета.
     *
     * Алгоритм:
     * - System: светлая или тёмная схема в зависимости от [isDark].
     * - Light: встроенная светлая схема.
     * - Dark: встроенная тёмная схема.
     * - Custom: вычисление через [ThemeColors.derive] из ключевых цветов.
     *
     * Если задано [textColor], переопределяет onSurface и onBackground.
     *
     * @param isDark Признак тёмной системной темы (используется только для пресета System).
     * @return Полная цветовая схема [ThemeColors].
     */
    fun themeColors(isDark: Boolean): ThemeColors {
        val baseColors = when (themePreset) {
            ThemePreset.System -> {
                if (isDark) ThemeColors.darkDefault() else ThemeColors.lightDefault()
            }
            ThemePreset.Light -> ThemeColors.lightDefault()
            ThemePreset.Dark -> ThemeColors.darkDefault()
            ThemePreset.Custom -> {
                val keys = keyColors ?: KeyColors.lightDefault()
                ThemeColors.derive(keys)
            }
        }

        return if (textColor != null) {
            baseColors.copy(
                onBackground = textColor,
                onSurface = textColor,
            )
        } else {
            baseColors
        }
    }
}