package com.overklassniy.stankinschedule.schedule.widget.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleItem
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.overklassniy.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * UseCase для конфигурирования виджета расписания.
 *
 * Предоставляет список доступных расписаний и операции загрузки/сохранения
 * конфигурации виджета.
 */
class ScheduleConfigureUseCase @Inject constructor(
    private val storage: ScheduleStorage,
    private val preference: ScheduleWidgetPreference
) {
    /**
     * Возвращает поток со списком доступных расписаний для конфигурации виджета.
     *
     * @return Flow со списком [ScheduleItem].
     */
    fun schedules() = storage.schedules().map { list ->
        list.map { item -> ScheduleItem(item.scheduleName, item.id) }
    }.flowOn(Dispatchers.IO)

    /**
     * Загружает данные конфигурации виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @return [ScheduleWidgetData] или null.
     */
    fun loadWidgetData(appWidgetId: Int): ScheduleWidgetData? =
        preference.loadData(appWidgetId)

    /**
     * Сохраняет данные конфигурации виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @param data Данные виджета.
     */
    fun saveWidgetData(appWidgetId: Int, data: ScheduleWidgetData) =
        preference.saveData(appWidgetId, data)
}