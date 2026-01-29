package com.overklassniy.stankinschedule.schedule.viewer.domain.repository

import androidx.paging.PagingSource
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair
import org.joda.time.LocalDate

/**
 * Репозиторий для подготовки данных расписания к отображению (Viewer).
 */
interface ScheduleViewerRepository {

    /**
     * Возвращает источник пагинации дней расписания.
     *
     * @param schedule Модель расписания.
     * @return [PagingSource] по датам, выдающий [ScheduleViewDay].
     */
    fun scheduleSource(schedule: ScheduleModel): PagingSource<LocalDate, ScheduleViewDay>

    /**
     * Конвертирует доменную пару в презентационную модель для Viewer.
     *
     * @param pair Доменная модель пары.
     * @return [ScheduleViewPair] для отображения.
     */
    fun convertToViewPair(pair: PairModel): ScheduleViewPair
}