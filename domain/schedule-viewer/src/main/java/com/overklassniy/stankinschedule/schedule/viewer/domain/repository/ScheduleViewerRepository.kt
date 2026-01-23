package com.overklassniy.stankinschedule.schedule.viewer.domain.repository

import androidx.paging.PagingSource
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair
import org.joda.time.LocalDate

interface ScheduleViewerRepository {

    fun scheduleSource(schedule: ScheduleModel): PagingSource<LocalDate, ScheduleViewDay>

    fun convertToViewPair(pair: PairModel): ScheduleViewPair
}