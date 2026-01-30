package com.overklassniy.stankinschedule.schedule.widget.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.overklassniy.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import kotlinx.coroutines.flow.first
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * UseCase для получения данных, необходимых виджету расписания.
 *
 * Формирует список дней/пар для выбранного расписания и подгруппы,
 * предоставляет цвета пар из настроек и загружает конфигурацию виджета.
 */
class ScheduleWidgetUseCase @Inject constructor(
    private val storage: ScheduleStorage,
    private val widgetPreference: ScheduleWidgetPreference,
    private val schedulePreference: SchedulePreference
) {

    /**
     * Формирует список дней с отфильтрованными парами по подгруппе.
     *
     * @param scheduleId ID расписания.
     * @param subgroup Подгруппа для фильтрации.
     * @param from Начальная дата (включительно).
     * @param to Конечная дата (не включительно).
     * @return Список списков пар по дням.
     */
    suspend fun scheduleDays(
        scheduleId: Long,
        subgroup: Subgroup,
        from: LocalDate,
        to: LocalDate
    ): List<List<PairModel>> {
        val model = storage.scheduleModel(scheduleId).first() ?: return emptyList()

        val days = mutableListOf<List<PairModel>>()

        var it = from
        while (it.isBefore(to)) {
            days += model.pairsByDate(it).filter { it.isCurrently(subgroup) }
            it = it.plusDays(1)
        }

        return days
    }

    /**
     * Возвращает текущую цветовую схему пар из настроек.
     *
     * @return [PairColorGroup] с HEX-цветами.
     */
    suspend fun pairColors(): PairColorGroup {
        return schedulePreference.scheduleColorGroup().first()
    }

    /**
     * Загружает сохранённые данные конфигурации виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return [ScheduleWidgetData] или null, если данных нет.
     */
    fun loadWidgetData(appWidgetId: Int): ScheduleWidgetData? =
        widgetPreference.loadData(appWidgetId)

}