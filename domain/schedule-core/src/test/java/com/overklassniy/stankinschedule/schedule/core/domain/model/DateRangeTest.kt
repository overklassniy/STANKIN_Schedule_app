package com.overklassniy.stankinschedule.schedule.core.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateRangeTest {

    @Test
    fun `intersect с одиночной датой внутри диапазона возвращает true`() {
        val range = DateRange("2025-09-01", "2025-09-15", Frequency.EVERY)
        assertTrue(range.intersect(DateSingle("2025-09-08")))
    }

    @Test
    fun `intersect с одиночной датой на границах диапазона возвращает true`() {
        val range = DateRange("2025-09-01", "2025-09-15", Frequency.EVERY)
        assertTrue(range.intersect(DateSingle("2025-09-01")))
        assertTrue(range.intersect(DateSingle("2025-09-15")))
    }

    @Test
    fun `intersect с одиночной датой вне диапазона возвращает false`() {
        val range = DateRange("2025-09-01", "2025-09-15", Frequency.EVERY)
        assertFalse(range.intersect(DateSingle("2025-09-10")))
        assertFalse(range.intersect(DateSingle("2025-09-16")))
    }

    @Test
    fun `intersect двух диапазонов с совпадающей датой возвращает true`() {
        val weekly = DateRange("2025-09-01", "2025-10-27", Frequency.EVERY)
        val biweekly = DateRange("2025-09-08", "2025-11-03", Frequency.THROUGHOUT)
        // Совпадение 2025-09-08 и 2025-09-22
        assertTrue(weekly.intersect(biweekly))
        assertTrue(biweekly.intersect(weekly))
    }

    @Test
    fun `intersect двух непересекающихся диапазонов возвращает false`() {
        val first = DateRange("2025-09-01", "2025-09-15", Frequency.EVERY)
        val second = DateRange("2025-09-03", "2025-09-17", Frequency.THROUGHOUT)
        // first: 01, 08, 15; second: 03, 17 — пересечений нет
        assertFalse(first.intersect(second))
        assertFalse(second.intersect(first))
    }

    @Test
    fun `intersect диапазона, который начинается после окончания другого, возвращает false`() {
        val first = DateRange("2025-09-01", "2025-09-15", Frequency.EVERY)
        val second = DateRange("2025-09-22", "2025-10-06", Frequency.EVERY)
        assertFalse(first.intersect(second))
    }
}
