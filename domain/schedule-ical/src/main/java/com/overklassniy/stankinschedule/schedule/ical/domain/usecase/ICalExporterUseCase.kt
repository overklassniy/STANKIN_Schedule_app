package com.overklassniy.stankinschedule.schedule.ical.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.model.DateItem
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateRange
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateSingle
import com.overklassniy.stankinschedule.schedule.core.domain.model.DayOfWeek
import com.overklassniy.stankinschedule.schedule.core.domain.model.Frequency
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.Time
import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalCalendar
import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalDate
import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalDayOfWeek
import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalEvent
import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalFrequency
import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalRecurrenceDate
import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalSingleDate
import com.overklassniy.stankinschedule.schedule.ical.domain.repository.ICalExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat
import javax.inject.Inject

class ICalExporterUseCase @Inject constructor(
    private val exporter: ICalExporter
) {
    private val dateTimeFormat = ISODateTimeFormat.basicDateTimeNoMillis()
    private val dateFormat = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss'Z'")

    /**
     * Экспортирует расписание в файл iCal.
     *
     * @param schedule Расписание для экспорта.
     * @param path Путь к файлу.
     * @return Flow, который эмитит true после успешного экспорта.
     */
    fun exportSchedule(schedule: ScheduleModel, path: String) = flow {
        val calendar = ICalCalendar(
            name = schedule.info.scheduleName,
            events = schedule.flatMap { pair -> eventFromPair(pair) }
        )
        exporter.export(calendar, path)
        emit(true)
    }.flowOn(Dispatchers.IO)

    private fun eventFromPair(pair: PairModel): List<ICalEvent> {
        return pair.date.map { date ->
            ICalEvent(
                summory = pair.title,
                description = pair.lecturer,
                location = pair.classroom,
                date = timeFromDate(date, pair.time)
            )
        }
    }

    private fun timeFromDate(date: DateItem, time: Time): ICalDate {
        return when (date) {
            is DateRange -> {
                ICalRecurrenceDate(
                    startTime = date.start.toDateTime(time.start).toString(dateTimeFormat),
                    endTime = date.start.toDateTime(time.end).toString(dateTimeFormat),
                    frequency = date.frequency().toICalFrequency(),
                    untilDate = date.end.toDateTime(LocalTime(23, 59, 59))
                        .toString(dateFormat),
                    byDay = date.dayOfWeek().toByDay()
                )
            }

            is DateSingle -> {
                ICalSingleDate(
                    startTime = date.date.toDateTime(time.start).toString(dateTimeFormat),
                    endTime = date.date.toDateTime(time.end).toString(dateTimeFormat),
                )
            }
        }
    }

    private fun Frequency.toICalFrequency(): ICalFrequency {
        return when (this) {
            Frequency.EVERY -> ICalFrequency.ICalWeekly(1)
            Frequency.THROUGHOUT -> ICalFrequency.ICalWeekly(2)
            else -> throw IllegalArgumentException("Invalid frequency")
        }
    }

    private fun DayOfWeek.toByDay(): ICalDayOfWeek {
        return when (this) {
            DayOfWeek.MONDAY -> ICalDayOfWeek.MO
            DayOfWeek.TUESDAY -> ICalDayOfWeek.TU
            DayOfWeek.WEDNESDAY -> ICalDayOfWeek.WE
            DayOfWeek.THURSDAY -> ICalDayOfWeek.TH
            DayOfWeek.FRIDAY -> ICalDayOfWeek.FR
            DayOfWeek.SATURDAY -> ICalDayOfWeek.SA
        }
    }
}