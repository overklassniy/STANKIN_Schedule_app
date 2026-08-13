package com.overklassniy.stankinschedule.schedule.widget.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import kotlinx.coroutines.flow.first
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * UseCase для получения данных расписания, необходимых Glance-виджету.
 *
 * Формирует список дней/пар для выбранного расписания и подгруппы.
 * Конфигурация виджета (цвета, пресет) хранится в Glance state и читается
 * непосредственно в ScheduleGlanceWidget.
 */
class ScheduleWidgetUseCase @Inject constructor(
    private val storage: ScheduleStorage
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
}