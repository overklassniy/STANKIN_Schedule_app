package com.overklassniy.stankinschedule.widget.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.overklassniy.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Реализация репозитория настроек виджета расписания.
 *
 * Сохраняет и загружает конфигурацию виджетов (ID расписания, подгруппу, отображение)
 * в SharedPreferences.
 *
 * @property context Контекст приложения.
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
            return ScheduleWidgetData(
                scheduleName = scheduleName,
                scheduleId = scheduleId,
                subgroup = Subgroup.of(subgroupTag),
                display = display
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
        }
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

    companion object {
        private const val SCHEDULE_WIDGET_PREFERENCE = "schedule_widget_preference"
        private const val SCHEDULE_WIDGET = "schedule_app_widget_"
        private const val NAME_SUFFIX = "_name"
        private const val ID_SUFFIX = "_id"
        private const val SUBGROUP_SUFFIX = "_subgroup"
        private const val DISPLAY_SUFFIX = "_display"
    }
}