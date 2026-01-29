package com.overklassniy.stankinschedule.schedule.table.domain.model

import com.overklassniy.stankinschedule.schedule.core.domain.model.DayOfWeek
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import org.joda.time.LocalDate

/**
 * Логическая модель таблицы расписания.
 *
 * Формирует структуру ячеек по дням недели на основе [ScheduleModel].
 */
class ScheduleTable {

    val days: Map<DayOfWeek, ScheduleTableDay> = buildMap(
        capacity = DayOfWeek.entries.size,
        builderAction = {
            DayOfWeek.entries.forEach { day ->
                this[day] = ScheduleTableDay()
            }
        }
    )

    val scheduleName: String
    val mode: TableMode

    /**
     * Создаёт таблицу для полного расписания (все дни недели).
     *
     * @param schedule Модель расписания.
     */
    constructor(schedule: ScheduleModel) {
        DayOfWeek.entries.forEach { day ->
            days[day]?.setPairs(schedule.pairsByDay(day))
        }

        scheduleName = schedule.info.scheduleName
        mode = TableMode.Full
    }

    /**
     * Создаёт таблицу для конкретной недели, начиная с понедельника указанной даты.
     *
     * @param schedule Модель расписания.
     * @param date Дата внутри недели.
     */
    constructor(schedule: ScheduleModel, date: LocalDate) {
        var currentDate = date.withDayOfWeek(1)

        DayOfWeek.entries.forEach { day ->
            days[day]?.setPairs(schedule.pairsByDate(currentDate))
            currentDate = currentDate.plusDays(1)
        }

        scheduleName = schedule.info.scheduleName +
                ". " + date.withDayOfWeek(1).toString("dd.MM.yyyy") +
                "-" + date.withDayOfWeek(7).toString("dd.MM.yyyy")
        mode = TableMode.Weekly
    }

    /**
     * Возвращает количество строк (подстрок) для каждого дня недели.
     *
     * @return Список чисел по дням недели.
     */
    fun linesPerDay(): List<Int> {
        return days.values.map { day -> day.lines() }
    }
}