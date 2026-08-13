package com.overklassniy.stankinschedule.schedule.widget.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * UseCase для конфигурирования виджета расписания.
 *
 * Предоставляет список доступных расписаний.
 * Загрузка и сохранение конфигурации виджета выполняются через Glance state
 * на уровне ViewModel, так как Glance API требует Android-контекста.
 */
class ScheduleConfigureUseCase @Inject constructor(
    private val storage: ScheduleStorage
) {
    /**
     * Возвращает поток со списком доступных расписаний для конфигурации виджета.
     *
     * @return Flow со списком [ScheduleItem].
     */
    fun schedules() = storage.schedules().map { list ->
        list.map { item -> ScheduleItem(item.scheduleName, item.id) }
    }.flowOn(Dispatchers.IO)
}