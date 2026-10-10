package com.overklassniy.stankinschedule.news.widget.ui.glance

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.overklassniy.stankinschedule.core.domain.settings.KeyColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemePreset
import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision
import com.overklassniy.stankinschedule.news.widget.domain.model.NewsWidgetData

/**
 * Ключи и маппинг состояния виджета новостей в Glance Preferences DataStore.
 *
 * Каждый параметр конфигурации виджета сохраняется под типизированным ключом.
 * Методы [toData] и [writeToPrefs] обеспечивают двустороннее преобразование
 * между [NewsWidgetData] и [Preferences].
 */
object NewsWidgetStateKeys {

    val subdivisions = stringSetPreferencesKey("news_subdivisions")
    val themePresetTag = stringPreferencesKey("news_theme_preset")

    val keyPrimary = stringPreferencesKey("news_key_primary")
    val keySecondary = stringPreferencesKey("news_key_secondary")
    val keyTertiary = stringPreferencesKey("news_key_tertiary")
    val keyBackground = stringPreferencesKey("news_key_background")
    val keySurface = stringPreferencesKey("news_key_surface")
    val keyError = stringPreferencesKey("news_key_error")

    val textColor = stringPreferencesKey("news_text_color")
    val announcementsColor = stringPreferencesKey("news_announcements_color")
    val isDarkTheme = booleanPreferencesKey("news_is_dark_theme")

    /**
     * Преобразует [Preferences] в [NewsWidgetData] или null, если данные отсутствуют.
     *
     * @param prefs Сохранённые настройки виджета.
     * @return Данные виджета или null.
     */
    fun toData(prefs: Preferences): NewsWidgetData? {
        val subdivisionsStr = prefs[subdivisions] ?: return null
        if (subdivisionsStr.isEmpty()) return null

        val selectedSubdivisions = subdivisionsStr
            .mapNotNull { idStr ->
                idStr.toIntOrNull()?.let { id ->
                    NewsSubdivision.entries.find { it.id == id }
                }
            }
            .toSet()

        if (selectedSubdivisions.isEmpty()) return null

        val preset = prefs[themePresetTag]?.let { ThemePreset.from(it) } ?: ThemePreset.System

        val keyColors = prefs.readKeyColors()
        val textColorOverride = prefs[textColor]?.takeIf { it.isNotEmpty() }
        val announcementsColorOverride = prefs[announcementsColor]?.takeIf { it.isNotEmpty() }
        val isDark = prefs[isDarkTheme] ?: false

        return NewsWidgetData(
            selectedSubdivisions = selectedSubdivisions,
            themePreset = preset,
            keyColors = if (preset == ThemePreset.Custom) keyColors else null,
            textColor = textColorOverride,
            announcementsColor = announcementsColorOverride,
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
     * Записывает [NewsWidgetData] в [Preferences] через мутатор.
     *
     * @param prefs Текущие настройки для мутации.
     * @param data Данные виджета для сохранения.
     */
    fun writeToPrefs(
        prefs: androidx.datastore.preferences.core.MutablePreferences,
        data: NewsWidgetData,
    ) {
        prefs[subdivisions] = data.selectedSubdivisions.map { it.id.toString() }.toSet()
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

        val textColorValue = data.textColor
        if (textColorValue != null) {
            prefs[textColor] = textColorValue
        } else {
            prefs.remove(textColor)
        }

        val announcementsColorValue = data.announcementsColor
        if (announcementsColorValue != null) {
            prefs[announcementsColor] = announcementsColorValue
        } else {
            prefs.remove(announcementsColor)
        }

        // isDarkTheme не записывается из конфигурации — это системный флаг,
        // управляемый из provideGlance и MainApplication.onConfigurationChanged
    }
}
