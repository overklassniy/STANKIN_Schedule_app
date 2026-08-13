package com.overklassniy.stankinschedule.widget.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.overklassniy.stankinschedule.core.domain.settings.KeyColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemePreset
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorType
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.overklassniy.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Реализация репозитория настроек виджета расписания.
 *
 * Сохраняет и загружает конфигурацию виджетов (ID расписания, подгруппу, отображение,
 * пресет темы, ключевые цвета, цвета пар) в SharedPreferences.
 *
 * @param context Контекст приложения.
 */
class ScheduleWidgetPreferenceImpl @Inject constructor(
    @ApplicationContext context: Context
) : ScheduleWidgetPreference {

    private val preference = context.getSharedPreferences(SCHEDULE_WIDGET_PREFERENCE, MODE_PRIVATE)

    /**
     * Загружает данные конфигурации для конкретного виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Данные виджета [ScheduleWidgetData] или null, если данные не найдены.
     */
    override fun loadData(appWidgetId: Int): ScheduleWidgetData? {
        val scheduleName = preference.getString(nameKey(appWidgetId), null)
        val scheduleId = preference.getLong(idKey(appWidgetId), -1L)
        val subgroupTag = preference.getString(subgroupTagKey(appWidgetId), Subgroup.COMMON.tag)
        val display = preference.getBoolean(displayKey(appWidgetId), true)

        if (scheduleName != null && scheduleId != -1L && subgroupTag != null) {
            val themePresetTag = preference.getString(themePresetKey(appWidgetId), ThemePreset.System.tag)
            val themePreset = ThemePreset.from(themePresetTag) ?: ThemePreset.System

            val keyColors = loadKeyColors(appWidgetId)
            val pairColorGroup = loadPairColorGroup(appWidgetId)
            val textColor = preference.getString(textColorKey(appWidgetId), null)
                ?.takeIf { it.isNotEmpty() }

            return ScheduleWidgetData(
                scheduleName = scheduleName,
                scheduleId = scheduleId,
                subgroup = Subgroup.of(subgroupTag),
                display = display,
                themePreset = themePreset,
                keyColors = keyColors,
                pairColorGroup = pairColorGroup,
                textColor = textColor,
            )
        }

        return null
    }

    /**
     * Сохраняет конфигурацию виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @param data Данные конфигурации для сохранения.
     */
    override fun saveData(appWidgetId: Int, data: ScheduleWidgetData) {
        preference.edit {
            putString(nameKey(appWidgetId), data.scheduleName)
            putLong(idKey(appWidgetId), data.scheduleId)
            putString(subgroupTagKey(appWidgetId), data.subgroup.tag)
            putBoolean(displayKey(appWidgetId), data.display)
            putString(themePresetKey(appWidgetId), data.themePreset.tag)

            val keys = data.keyColors
            if (keys != null) {
                putString(keyPrimaryKey(appWidgetId), keys.primary)
                putString(keySecondaryKey(appWidgetId), keys.secondary)
                putString(keyTertiaryKey(appWidgetId), keys.tertiary)
                putString(keyBackgroundKey(appWidgetId), keys.background)
                putString(keySurfaceKey(appWidgetId), keys.surface)
                putString(keyErrorKey(appWidgetId), keys.error)
            } else {
                remove(keyPrimaryKey(appWidgetId))
                remove(keySecondaryKey(appWidgetId))
                remove(keyTertiaryKey(appWidgetId))
                remove(keyBackgroundKey(appWidgetId))
                remove(keySurfaceKey(appWidgetId))
                remove(keyErrorKey(appWidgetId))
            }

            putString(pairLectureKey(appWidgetId), data.pairColorGroup.lectureColor)
            putString(pairSeminarKey(appWidgetId), data.pairColorGroup.seminarColor)
            putString(pairLaboratoryKey(appWidgetId), data.pairColorGroup.laboratoryColor)
            putString(pairSubgroupAKey(appWidgetId), data.pairColorGroup.subgroupAColor)
            putString(pairSubgroupBKey(appWidgetId), data.pairColorGroup.subgroupBColor)

            val textColorValue = data.textColor
            if (textColorValue != null) {
                putString(textColorKey(appWidgetId), textColorValue)
            } else {
                remove(textColorKey(appWidgetId))
            }
        }
    }

    /**
     * Удаляет данные конфигурации виджета.
     *
     * Используется при удалении виджета с рабочего стола.
     *
     * @param appWidgetId Идентификатор виджета.
     */
    override fun deleteData(appWidgetId: Int) {
        preference.edit {
            remove(nameKey(appWidgetId))
            remove(idKey(appWidgetId))
            remove(subgroupTagKey(appWidgetId))
            remove(displayKey(appWidgetId))
            remove(themePresetKey(appWidgetId))
            remove(keyPrimaryKey(appWidgetId))
            remove(keySecondaryKey(appWidgetId))
            remove(keyTertiaryKey(appWidgetId))
            remove(keyBackgroundKey(appWidgetId))
            remove(keySurfaceKey(appWidgetId))
            remove(keyErrorKey(appWidgetId))
            remove(pairLectureKey(appWidgetId))
            remove(pairSeminarKey(appWidgetId))
            remove(pairLaboratoryKey(appWidgetId))
            remove(pairSubgroupAKey(appWidgetId))
            remove(pairSubgroupBKey(appWidgetId))
            remove(textColorKey(appWidgetId))
        }
    }

    /**
     * Загружает ключевые цвета темы виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return [KeyColors] или null, если ключевые цвета не сохранены.
     */
    private fun loadKeyColors(appWidgetId: Int): KeyColors? {
        val primary = preference.getString(keyPrimaryKey(appWidgetId), null) ?: return null
        val secondary = preference.getString(keySecondaryKey(appWidgetId), null) ?: return null
        val tertiary = preference.getString(keyTertiaryKey(appWidgetId), null) ?: return null
        val background = preference.getString(keyBackgroundKey(appWidgetId), null) ?: return null
        val surface = preference.getString(keySurfaceKey(appWidgetId), null) ?: return null
        val error = preference.getString(keyErrorKey(appWidgetId), null) ?: return null

        return KeyColors(
            primary = primary,
            secondary = secondary,
            tertiary = tertiary,
            background = background,
            surface = surface,
            error = error,
        )
    }

    /**
     * Загружает цвета пар виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return [PairColorGroup] с сохранёнными или значениями по умолчанию.
     */
    private fun loadPairColorGroup(appWidgetId: Int): PairColorGroup {
        return PairColorGroup(
            lectureColor = preference.getString(pairLectureKey(appWidgetId), null) ?: PairColorType.Lecture.hex,
            seminarColor = preference.getString(pairSeminarKey(appWidgetId), null) ?: PairColorType.Seminar.hex,
            laboratoryColor = preference.getString(pairLaboratoryKey(appWidgetId), null) ?: PairColorType.Laboratory.hex,
            subgroupAColor = preference.getString(pairSubgroupAKey(appWidgetId), null) ?: PairColorType.SubgroupA.hex,
            subgroupBColor = preference.getString(pairSubgroupBKey(appWidgetId), null) ?: PairColorType.SubgroupB.hex,
        )
    }

    /**
     * Генерирует ключ для сохранения имени расписания.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для имени.
     */
    private fun nameKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + NAME_SUFFIX

    /**
     * Генерирует ключ для сохранения ID расписания.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для ID.
     */
    private fun idKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + ID_SUFFIX

    /**
     * Генерирует ключ для сохранения тега подгруппы.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для подгруппы.
     */
    private fun subgroupTagKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + SUBGROUP_SUFFIX

    /**
     * Генерирует ключ для сохранения настройки отображения.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для флага отображения.
     */
    private fun displayKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + DISPLAY_SUFFIX

    /**
     * Генерирует ключ для сохранения пресета темы виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для пресета темы.
     */
    private fun themePresetKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + THEME_PRESET_SUFFIX

    /**
     * Генерирует ключ для сохранения ключевого цвета primary.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для primary.
     */
    private fun keyPrimaryKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + KEY_PRIMARY_SUFFIX

    /**
     * Генерирует ключ для сохранения ключевого цвета secondary.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для secondary.
     */
    private fun keySecondaryKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + KEY_SECONDARY_SUFFIX

    /**
     * Генерирует ключ для сохранения ключевого цвета tertiary.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для tertiary.
     */
    private fun keyTertiaryKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + KEY_TERTIARY_SUFFIX

    /**
     * Генерирует ключ для сохранения ключевого цвета background.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для background.
     */
    private fun keyBackgroundKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + KEY_BACKGROUND_SUFFIX

    /**
     * Генерирует ключ для сохранения ключевого цвета surface.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для surface.
     */
    private fun keySurfaceKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + KEY_SURFACE_SUFFIX

    /**
     * Генерирует ключ для сохранения ключевого цвета error.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для error.
     */
    private fun keyErrorKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + KEY_ERROR_SUFFIX

    /**
     * Генерирует ключ для сохранения цвета лекции.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для цвета лекции.
     */
    private fun pairLectureKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + PAIR_LECTURE_SUFFIX

    /**
     * Генерирует ключ для сохранения цвета семинара.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для цвета семинара.
     */
    private fun pairSeminarKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + PAIR_SEMINAR_SUFFIX

    /**
     * Генерирует ключ для сохранения цвета лабораторной.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для цвета лабораторной.
     */
    private fun pairLaboratoryKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + PAIR_LABORATORY_SUFFIX

    /**
     * Генерирует ключ для сохранения цвета подгруппы A.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для цвета подгруппы A.
     */
    private fun pairSubgroupAKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + PAIR_SUBGROUP_A_SUFFIX

    /**
     * Генерирует ключ для сохранения цвета подгруппы B.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для цвета подгруппы B.
     */
    private fun pairSubgroupBKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + PAIR_SUBGROUP_B_SUFFIX

    /**
     * Генерирует ключ для сохранения цвета текста.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return Ключ preferences для цвета текста.
     */
    private fun textColorKey(appWidgetId: Int): String =
        SCHEDULE_WIDGET + appWidgetId + TEXT_COLOR_SUFFIX

    companion object {
        private const val SCHEDULE_WIDGET_PREFERENCE = "schedule_widget_preference"
        private const val SCHEDULE_WIDGET = "schedule_app_widget_"
        private const val NAME_SUFFIX = "_name"
        private const val ID_SUFFIX = "_id"
        private const val SUBGROUP_SUFFIX = "_subgroup"
        private const val DISPLAY_SUFFIX = "_display"
        private const val THEME_PRESET_SUFFIX = "_theme_preset"
        private const val KEY_PRIMARY_SUFFIX = "_key_primary"
        private const val KEY_SECONDARY_SUFFIX = "_key_secondary"
        private const val KEY_TERTIARY_SUFFIX = "_key_tertiary"
        private const val KEY_BACKGROUND_SUFFIX = "_key_background"
        private const val KEY_SURFACE_SUFFIX = "_key_surface"
        private const val KEY_ERROR_SUFFIX = "_key_error"
        private const val PAIR_LECTURE_SUFFIX = "_pair_lecture"
        private const val PAIR_SEMINAR_SUFFIX = "_pair_seminar"
        private const val PAIR_LABORATORY_SUFFIX = "_pair_laboratory"
        private const val PAIR_SUBGROUP_A_SUFFIX = "_pair_subgroup_a"
        private const val PAIR_SUBGROUP_B_SUFFIX = "_pair_subgroup_b"
        private const val TEXT_COLOR_SUFFIX = "_text_color"
    }
}