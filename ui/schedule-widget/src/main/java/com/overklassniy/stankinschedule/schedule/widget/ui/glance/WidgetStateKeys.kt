package com.overklassniy.stankinschedule.schedule.widget.ui.glance

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.overklassniy.stankinschedule.core.domain.settings.KeyColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemePreset
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorType
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData

/**
 * Ключи и маппинг состояния виджета в Glance Preferences DataStore.
 *
 * Каждый параметр конфигурации виджета сохраняется под типизированным ключом.
 * Методы [toData] и [writeToPrefs] обеспечивают двустороннее преобразование
 * между [ScheduleWidgetData] и [Preferences].
 */
object WidgetStateKeys {

    val scheduleName = stringPreferencesKey("schedule_name")
    val scheduleId = longPreferencesKey("schedule_id")
    val subgroupTag = stringPreferencesKey("subgroup_tag")
    val display = booleanPreferencesKey("display")
    val themePresetTag = stringPreferencesKey("theme_preset")

    val keyPrimary = stringPreferencesKey("key_primary")
    val keySecondary = stringPreferencesKey("key_secondary")
    val keyTertiary = stringPreferencesKey("key_tertiary")
    val keyBackground = stringPreferencesKey("key_background")
    val keySurface = stringPreferencesKey("key_surface")
    val keyError = stringPreferencesKey("key_error")

    val pairLecture = stringPreferencesKey("pair_lecture")
    val pairSeminar = stringPreferencesKey("pair_seminar")
    val pairLaboratory = stringPreferencesKey("pair_laboratory")
    val pairSubgroupA = stringPreferencesKey("pair_subgroup_a")
    val pairSubgroupB = stringPreferencesKey("pair_subgroup_b")
    val textColor = stringPreferencesKey("text_color")
    val isDarkTheme = booleanPreferencesKey("is_dark_theme")

    /**
     * Преобразует [Preferences] в [ScheduleWidgetData] или null, если данные отсутствуют.
     *
     * @param prefs Сохранённые настройки виджета.
     * @return Данные виджета или null.
     */
    fun toData(prefs: Preferences): ScheduleWidgetData? {
        val name = prefs[scheduleName] ?: return null
        val id = prefs[scheduleId] ?: return null
        val subgroup = prefs[subgroupTag]?.let { runCatching { Subgroup.of(it) }.getOrNull() }
            ?: Subgroup.COMMON
        val isDisplay = prefs[display] ?: true
        val preset = prefs[themePresetTag]?.let { ThemePreset.from(it) } ?: ThemePreset.System

        val keyColors = prefs.readKeyColors()
        val pairColorGroup = prefs.readPairColorGroup()
        val textColorOverride = prefs[textColor]?.takeIf { it.isNotEmpty() }
        val isDark = prefs[isDarkTheme] ?: false

        return ScheduleWidgetData(
            scheduleName = name,
            scheduleId = id,
            subgroup = subgroup,
            display = isDisplay,
            themePreset = preset,
            keyColors = if (preset == ThemePreset.Custom) keyColors else null,
            pairColorGroup = pairColorGroup,
            textColor = textColorOverride,
            isDarkTheme = isDark,
        )
    }

    /**
     * Читает пользовательские ключевые цвета из настроек или null, если не заданы.
     */
    private fun Preferences.readKeyColors(): KeyColors? {
        val defaults = KeyColors.lightDefault()
        return this@readKeyColors[keyPrimary]?.let { primary ->
            KeyColors(
                primary = primary,
                secondary = this[keySecondary] ?: defaults.secondary,
                tertiary = this[keyTertiary] ?: defaults.tertiary,
                background = this[keyBackground] ?: defaults.background,
                surface = this[keySurface] ?: defaults.surface,
                error = this[keyError] ?: defaults.error,
            )
        }
    }

    /**
     * Читает цвета типов пар из настроек с подстановкой значений по умолчанию.
     */
    private fun Preferences.readPairColorGroup(): PairColorGroup {
        return PairColorGroup(
            lectureColor = this[pairLecture] ?: PairColorType.Lecture.hex,
            seminarColor = this[pairSeminar] ?: PairColorType.Seminar.hex,
            laboratoryColor = this[pairLaboratory] ?: PairColorType.Laboratory.hex,
            subgroupAColor = this[pairSubgroupA] ?: PairColorType.SubgroupA.hex,
            subgroupBColor = this[pairSubgroupB] ?: PairColorType.SubgroupB.hex,
        )
    }

    /**
     * Записывает [ScheduleWidgetData] в [Preferences] через мутатор.
     *
     * @param prefs Текущие настройки для мутации.
     * @param data Данные виджета для сохранения.
     */
    fun writeToPrefs(prefs: androidx.datastore.preferences.core.MutablePreferences, data: ScheduleWidgetData) {
        prefs[scheduleName] = data.scheduleName
        prefs[scheduleId] = data.scheduleId
        prefs[subgroupTag] = data.subgroup.tag
        prefs[display] = data.display
        prefs[themePresetTag] = data.themePreset.tag

        val keys = data.keyColors
        if (keys != null) {
            prefs[keyPrimary] = keys.primary
            prefs[keySecondary] = keys.secondary
            prefs[keyTertiary] = keys.tertiary
            prefs[keyBackground] = keys.background
            prefs[keySurface] = keys.surface
            prefs[keyError] = keys.error
        } else {
            prefs.remove(keyPrimary)
            prefs.remove(keySecondary)
            prefs.remove(keyTertiary)
            prefs.remove(keyBackground)
            prefs.remove(keySurface)
            prefs.remove(keyError)
        }

        prefs[pairLecture] = data.pairColorGroup.lectureColor
        prefs[pairSeminar] = data.pairColorGroup.seminarColor
        prefs[pairLaboratory] = data.pairColorGroup.laboratoryColor
        prefs[pairSubgroupA] = data.pairColorGroup.subgroupAColor
        prefs[pairSubgroupB] = data.pairColorGroup.subgroupBColor

        val textColorValue = data.textColor
        if (textColorValue != null) {
            prefs[textColor] = textColorValue
        } else {
            prefs.remove(textColor)
        }

        // isDarkTheme не записывается из конфигурации — это системный флаг,
        // управляемый из provideGlance и MainApplication.onConfigurationChanged
    }
}
