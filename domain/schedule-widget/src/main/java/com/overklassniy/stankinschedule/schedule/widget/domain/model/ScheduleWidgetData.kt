package com.overklassniy.stankinschedule.schedule.widget.domain.model

import com.overklassniy.stankinschedule.core.domain.settings.KeyColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemeColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemePreset
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup

/**
 * Данные для конфигурации виджета расписания.
 *
 * @property scheduleName Название расписания.
 * @property scheduleId Идентификатор расписания.
 * @property subgroup Подгруппа для отображения.
 * @property display Флаг отображения виджета.
 * @property themePreset Пресет темы виджета. Независим от темы приложения.
 * @property keyColors Ключевые цвета для пресета Custom. null для остальных пресетов.
 * @property pairColorGroup Цвета пар виджета. Независимы от настроек приложения.
 * @property textColor Переопределение цвета текста (onSurface и onBackground). null для автоматического вычисления.
 * @property isDarkTheme Признак тёмной системной темы. Сохраняется в Glance state для реагирования на смену темы.
 */
data class ScheduleWidgetData(
    val scheduleName: String,
    val scheduleId: Long,
    val subgroup: Subgroup,
    val display: Boolean,
    val themePreset: ThemePreset = ThemePreset.System,
    val keyColors: KeyColors? = null,
    val pairColorGroup: PairColorGroup = PairColorGroup.default(),
    val textColor: String? = null,
    val isDarkTheme: Boolean = false,
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
