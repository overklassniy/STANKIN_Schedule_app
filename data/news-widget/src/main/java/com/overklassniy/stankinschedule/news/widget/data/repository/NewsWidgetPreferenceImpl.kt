package com.overklassniy.stankinschedule.news.widget.data.repository

import android.content.Context
import androidx.core.content.edit
import com.overklassniy.stankinschedule.core.domain.settings.KeyColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemePreset
import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision
import com.overklassniy.stankinschedule.news.widget.domain.model.NewsWidgetData
import com.overklassniy.stankinschedule.news.widget.domain.repository.NewsWidgetPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Legacy-реализация хранилища настроек виджета новостей через SharedPreferences.
 *
 * Используется только для одноразовой миграции в Glance state при первом рендеринге.
 * Основное хранение осуществляется через Glance Preferences DataStore.
 */
class NewsWidgetPreferenceImpl @Inject constructor(
    @ApplicationContext context: Context,
) : NewsWidgetPreference {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Загружает сохранённые данные для виджета из SharedPreferences.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return [NewsWidgetData] или null, если данных нет.
     */
    override fun loadData(appWidgetId: Int): NewsWidgetData? {
        val subdivisionsStr = prefs.getString(keySubdivisions(appWidgetId), null)
            ?: return null

        val subdivisions = subdivisionsStr.split(",")
            .filter { it.isNotEmpty() }
            .mapNotNull { idStr ->
                idStr.toIntOrNull()?.let { id ->
                    NewsSubdivision.entries.find { it.id == id }
                }
            }
            .toSet()

        if (subdivisions.isEmpty()) return null

        val presetTag = prefs.getString(keyThemePreset(appWidgetId), null)
        val preset = presetTag?.let { ThemePreset.from(it) } ?: ThemePreset.System

        val keyColors = prefs.getString(keyPrimary(appWidgetId), null)?.let { primary ->
            KeyColors(
                primary = primary,
                secondary = prefs.getString(keySecondary(appWidgetId), null) ?: KeyColors.lightDefault().secondary,
                tertiary = prefs.getString(keyTertiary(appWidgetId), null) ?: KeyColors.lightDefault().tertiary,
                background = prefs.getString(keyBackground(appWidgetId), null) ?: KeyColors.lightDefault().background,
                surface = prefs.getString(keySurface(appWidgetId), null) ?: KeyColors.lightDefault().surface,
                error = prefs.getString(keyError(appWidgetId), null) ?: KeyColors.lightDefault().error,
            )
        }

        val textColor = prefs.getString(keyTextColor(appWidgetId), null)?.takeIf { it.isNotEmpty() }
        val announcementsColor = prefs.getString(keyAnnouncementsColor(appWidgetId), null)?.takeIf { it.isNotEmpty() }

        return NewsWidgetData(
            selectedSubdivisions = subdivisions,
            themePreset = preset,
            keyColors = if (preset == ThemePreset.Custom) keyColors else null,
            textColor = textColor,
            announcementsColor = announcementsColor,
        )
    }

    /**
     * Сохраняет данные конфигурации виджета в SharedPreferences.
     *
     * @param appWidgetId Идентификатор виджета.
     * @param data Данные виджета для сохранения.
     */
    override fun saveData(appWidgetId: Int, data: NewsWidgetData) {
        prefs.edit {
            putString(keySubdivisions(appWidgetId), data.selectedSubdivisions.joinToString(",") { it.id.toString() })
            putString(keyThemePreset(appWidgetId), data.themePreset.tag)

            val keys = data.keyColors
            if (keys != null) {
                putString(keyPrimary(appWidgetId), keys.primary)
                putString(keySecondary(appWidgetId), keys.secondary)
                putString(keyTertiary(appWidgetId), keys.tertiary)
                putString(keyBackground(appWidgetId), keys.background)
                putString(keySurface(appWidgetId), keys.surface)
                putString(keyError(appWidgetId), keys.error)
            }

            val textColorValue = data.textColor
            if (textColorValue != null) {
                putString(keyTextColor(appWidgetId), textColorValue)
            }

            val announcementsColorValue = data.announcementsColor
            if (announcementsColorValue != null) {
                putString(keyAnnouncementsColor(appWidgetId), announcementsColorValue)
            }
        }
    }

    /**
     * Удаляет сохранённые данные виджета из SharedPreferences.
     *
     * @param appWidgetId Идентификатор виджета.
     */
    override fun deleteData(appWidgetId: Int) {
        prefs.edit {
            remove(keySubdivisions(appWidgetId))
            remove(keyThemePreset(appWidgetId))
            remove(keyPrimary(appWidgetId))
            remove(keySecondary(appWidgetId))
            remove(keyTertiary(appWidgetId))
            remove(keyBackground(appWidgetId))
            remove(keySurface(appWidgetId))
            remove(keyError(appWidgetId))
            remove(keyTextColor(appWidgetId))
            remove(keyAnnouncementsColor(appWidgetId))
        }
    }

    private fun keySubdivisions(id: Int) = "news_widget_subdivisions_$id"
    private fun keyThemePreset(id: Int) = "news_widget_theme_preset_$id"
    private fun keyPrimary(id: Int) = "news_widget_primary_$id"
    private fun keySecondary(id: Int) = "news_widget_secondary_$id"
    private fun keyTertiary(id: Int) = "news_widget_tertiary_$id"
    private fun keyBackground(id: Int) = "news_widget_background_$id"
    private fun keySurface(id: Int) = "news_widget_surface_$id"
    private fun keyError(id: Int) = "news_widget_error_$id"
    private fun keyTextColor(id: Int) = "news_widget_text_color_$id"
    private fun keyAnnouncementsColor(id: Int) = "news_widget_announcements_color_$id"

    private companion object {
        private const val PREFS_NAME = "news_widget_prefs"
    }
}