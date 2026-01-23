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

class ScheduleWidgetUseCase @Inject constructor(
    private val storage: ScheduleStorage,
    private val widgetPreference: ScheduleWidgetPreference,
    private val schedulePreference: SchedulePreference
) {

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

    suspend fun pairColors(): PairColorGroup {
        return schedulePreference.scheduleColorGroup().first()
    }

    fun loadWidgetData(appWidgetId: Int): ScheduleWidgetData? =
        widgetPreference.loadData(appWidgetId)

}