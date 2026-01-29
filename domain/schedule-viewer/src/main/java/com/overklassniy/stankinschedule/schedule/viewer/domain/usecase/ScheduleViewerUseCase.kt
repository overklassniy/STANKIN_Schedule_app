package com.overklassniy.stankinschedule.schedule.viewer.domain.usecase

import androidx.paging.PagingSource
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.overklassniy.stankinschedule.schedule.viewer.domain.repository.ScheduleViewerRepository
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * UseCase для получения данных расписания в формате, удобном для Viewer.
 *
 * Предоставляет источник пагинации и построение списка дней за интервал.
 */
class ScheduleViewerUseCase @Inject constructor(
    private val repository: ScheduleViewerRepository
) {
    /**
     * Возвращает источник пагинации для отображения расписания по дням.
     *
     * @param model Модель расписания.
     * @return [PagingSource] по датам с элементами [ScheduleViewDay].
     */
    fun scheduleSource(model: ScheduleModel): PagingSource<LocalDate, ScheduleViewDay> =
        repository.scheduleSource(model)

    /**
     * Формирует список дней для указанного интервала [from; to).
     *
     * @param model Модель расписания.
     * @param from Начальная дата (включительно).
     * @param to Конечная дата (не включительно).
     * @return Список [ScheduleViewDay].
     */
    fun scheduleViewDays(
        model: ScheduleModel,
        from: LocalDate,
        to: LocalDate
    ): List<ScheduleViewDay> {
        val days = mutableListOf<ScheduleViewDay>()

        var it = from
        while (it.isBefore(to)) {

            days += ScheduleViewDay(
                day = it,
                pairs = model.pairsByDate(it).map { repository.convertToViewPair(it) }
            )

            it = it.plusDays(1)
        }

        return days
    }
}