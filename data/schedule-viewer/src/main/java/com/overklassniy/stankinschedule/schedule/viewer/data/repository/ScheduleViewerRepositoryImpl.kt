package com.overklassniy.stankinschedule.schedule.viewer.data.repository

import androidx.paging.PagingSource
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.viewer.data.mapper.toViewPair
import com.overklassniy.stankinschedule.schedule.viewer.data.source.ScheduleViewerSource
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair
import com.overklassniy.stankinschedule.schedule.viewer.domain.repository.ScheduleViewerRepository
import org.joda.time.LocalDate
import javax.inject.Inject

class ScheduleViewerRepositoryImpl @Inject constructor() : ScheduleViewerRepository {

    override fun scheduleSource(schedule: ScheduleModel): PagingSource<LocalDate, ScheduleViewDay> {
        return ScheduleViewerSource(schedule)
    }

    override fun convertToViewPair(pair: PairModel): ScheduleViewPair {
        return pair.toViewPair()
    }
}