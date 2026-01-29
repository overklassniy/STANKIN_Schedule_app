package com.overklassniy.stankinschedule.schedule.viewer.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.paging.PagingSource
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.viewer.data.mapper.toViewPair
import com.overklassniy.stankinschedule.schedule.viewer.data.source.ScheduleViewerSource
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair
import com.overklassniy.stankinschedule.schedule.viewer.domain.repository.ScheduleViewerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * Реализация репозитория просмотра расписания.
 *
 * Отвечает за создание источника данных и маппинг моделей.
 *
 * @property context Контекст приложения (используется для определения режима отладки).
 */
class ScheduleViewerRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : ScheduleViewerRepository {

    private val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    /**
     * Создает источник данных для пейджинга.
     *
     * @param schedule Модель расписания.
     * @return Источник данных [PagingSource] для пейджинга по датам.
     */
    override fun scheduleSource(schedule: ScheduleModel): PagingSource<LocalDate, ScheduleViewDay> {
        return ScheduleViewerSource(schedule, isDebug)
    }

    /**
     * Преобразует модель пары ядра в модель отображения.
     *
     * @param pair Модель пары [PairModel].
     * @return Модель пары для отображения [ScheduleViewPair].
     */
    override fun convertToViewPair(pair: PairModel): ScheduleViewPair {
        return pair.toViewPair()
    }
}