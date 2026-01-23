package com.overklassniy.stankinschedule.schedule.widget.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleItem
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.overklassniy.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScheduleConfigureUseCase @Inject constructor(
    private val storage: ScheduleStorage,
    private val preference: ScheduleWidgetPreference
) {
    fun schedules() = storage.schedules().map { list ->
        list.map { item -> ScheduleItem(item.scheduleName, item.id) }
    }.flowOn(Dispatchers.IO)

    fun loadWidgetData(appWidgetId: Int): ScheduleWidgetData? =
        preference.loadData(appWidgetId)

    fun saveWidgetData(appWidgetId: Int, data: ScheduleWidgetData) =
        preference.saveData(appWidgetId, data)
}