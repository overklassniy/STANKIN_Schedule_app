package com.overklassniy.stankinschedule.schedule.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import org.joda.time.LocalDate

class ScheduleModelTest {

    private fun scheduleWithRange(start: String, end: String): ScheduleModel {
        val model = ScheduleModel(ScheduleInfo("test"))
        val pair = PairModel(
            title = "Предмет",
            lecturer = "Иванов И.И.",
            classroom = "101",
            type = Type.LECTURE,
            subgroup = Subgroup.COMMON,
            time = Time.fromString("8:30-10:05"),
            date = DateModel().apply { add(DateRange(start, end, Frequency.EVERY)) },
        )
        model.add(pair)
        return model
    }

    @Test
    fun `limitDate возвращает начало расписания для даты до его начала`() {
        val model = scheduleWithRange("2025-09-01", "2025-09-15")
        assertEquals(LocalDate("2025-09-01"), model.limitDate(LocalDate("2025-08-20")))
    }

    @Test
    fun `limitDate возвращает конец расписания для даты после его окончания`() {
        val model = scheduleWithRange("2025-09-01", "2025-09-15")
        assertEquals(LocalDate("2025-09-15"), model.limitDate(LocalDate("2025-10-01")))
    }

    @Test
    fun `limitDate возвращает саму дату внутри диапазона расписания`() {
        val model = scheduleWithRange("2025-09-01", "2025-09-15")
        assertEquals(LocalDate("2025-09-08"), model.limitDate(LocalDate("2025-09-08")))
    }

    @Test
    fun `limitDate для пустого расписания возвращает саму дату`() {
        val model = ScheduleModel(ScheduleInfo("empty"))
        assertEquals(LocalDate("2025-09-08"), model.limitDate(LocalDate("2025-09-08")))
    }
}
