package com.overklassniy.stankinschedule.ical.data.repository

import android.content.Context
import androidx.core.net.toUri
import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalCalendar
import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalEvent
import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalFrequency
import com.overklassniy.stankinschedule.schedule.ical.domain.model.ICalRecurrenceDate
import com.overklassniy.stankinschedule.schedule.ical.domain.repository.ICalExporter
import dagger.hilt.android.qualifiers.ApplicationContext
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.ParameterList
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.PropertyList
import net.fortuna.ical4j.model.Recur
import net.fortuna.ical4j.model.UtcOffset
import net.fortuna.ical4j.model.component.Standard
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.model.property.CalScale
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.model.property.DtEnd
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.model.property.Location
import net.fortuna.ical4j.model.property.Method
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.RRule
import net.fortuna.ical4j.model.property.Sequence
import net.fortuna.ical4j.model.property.Status
import net.fortuna.ical4j.model.property.Summary
import net.fortuna.ical4j.model.property.Transp
import net.fortuna.ical4j.model.property.TzId
import net.fortuna.ical4j.model.property.TzName
import net.fortuna.ical4j.model.property.TzOffsetFrom
import net.fortuna.ical4j.model.property.TzOffsetTo
import net.fortuna.ical4j.model.property.Uid
import net.fortuna.ical4j.model.property.Version
import net.fortuna.ical4j.model.property.XProperty
import net.fortuna.ical4j.util.RandomUidGenerator
import javax.inject.Inject


/**
 * Репозиторий для работы с экспортом расписания в формат iCal (.ics).
 *
 * @param context Контекст приложения для доступа к ContentResolver
 */
class ICalRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : ICalExporter {

    /**
     * Экспортирует календарь в файл iCal по указанному пути.
     *
     * Алгоритм:
     * 1. Создает объект Calendar из библиотеки ical4j.
     * 2. Заполняет свойства календаря (ProdId, Version, TimeZone и т.д.).
     * 3. Добавляет VTimeZone.
     * 4. Генерирует и добавляет события (VEvent) из переданного календаря.
     * 5. Записывает полученную строку календаря в файл по URI.
     *
     * @param calendar Объект календаря с данными расписания
     * @param path Строковый URI, куда нужно сохранить файл
     * @throws IllegalAccessException Если не удалось получить дескриптор файла
     */
    override suspend fun export(calendar: ICalCalendar, path: String) {
        val ical = Calendar().apply {
            properties.apply {
                add(ProdId(calendar.prodId))
                add(Version.VERSION_2_0)
                add(CalScale.GREGORIAN)
                add(Method.PUBLISH)
                add(XProperty("X-WR-CALNAME", calendar.name))
                add(XProperty("X-WR-CALDESC", "Расписание ${calendar.name}"))
                add(XProperty("X-WR-TIMEZONE", calendar.timeZone))
            }


            components.apply {
                add(
                    VTimeZone().apply {
                        observances.apply {
                            add(Standard(PropertyList<Property>().apply {
                                add(TzName(calendar.timeZoneName))
                                add(TzOffsetFrom(UtcOffset(calendar.timeZoneOffset)))
                                add(TzOffsetTo(UtcOffset(calendar.timeZoneOffset)))
                                add(DtStart("19700101T000000"))
                            }))
                        }
                        properties.apply {
                            add(TzId(calendar.timeZone))
                            add(XProperty("X-LIC-LOCATION", calendar.timeZone))
                        }
                    }
                )

                val generator = RandomUidGenerator()
                calendar.events.forEach { event ->
                    add(createEvent(generator.generateUid(), calendar, event))
                }
            }
        }.toString()

        context.contentResolver.openOutputStream(path.toUri()).use { stream ->
            if (stream == null) throw IllegalAccessException("Failed to get file descriptor")

            stream.bufferedWriter().use { writer ->
                writer.write(ical)
            }
        }
    }
}

private fun createEvent(uid: Uid, calendar: ICalCalendar, event: ICalEvent): VEvent {
    return VEvent().apply {
        properties.apply {
            add(uid)
            add(Summary(event.summory))
            add(Description(event.description))
            add(Location(event.location))
            add(DtStart(timeZoneParameterList(calendar.timeZone), event.date.startTime))
            add(DtEnd(timeZoneParameterList(calendar.timeZone), event.date.endTime))
            add(Sequence(0))
            add(Status.VEVENT_CONFIRMED)
            add(Transp.OPAQUE)

            if (event.date is ICalRecurrenceDate) {
                add(createRRule(event.date as ICalRecurrenceDate))
            }
        }
    }
}

/**
 * Создает правило повторения события (RRule).
 *
 * Алгоритм:
 * 1. Формирует строку правила (FREQ, UNTIL, INTERVAL, BYDAY).
 * 2. Создает объект RRule.
 *
 * @param date Дата с параметрами повторения
 * @return RRule Объект правила повторения ical4j
 */
private fun createRRule(date: ICalRecurrenceDate): RRule {
    return RRule(
        Recur(
            buildString {
                append("FREQ=WEEKLY;")
                append("UNTIL=${date.untilDate};")
                if (date.frequency is ICalFrequency.ICalWeekly) {
                    val interval = (date.frequency as ICalFrequency.ICalWeekly).interval
                    if (interval > 1) {
                        append("INTERVAL=$interval;")
                    }
                }
                append("BYDAY=${date.byDay.tag}")
            }
        )
    )
}

private fun timeZoneParameterList(timeZone: String): ParameterList {
    return ParameterList().apply {
        add(net.fortuna.ical4j.model.parameter.TzId(timeZone))
    }
}