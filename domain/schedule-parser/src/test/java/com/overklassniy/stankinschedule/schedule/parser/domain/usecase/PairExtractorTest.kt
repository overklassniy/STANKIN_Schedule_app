package com.overklassniy.stankinschedule.schedule.parser.domain.usecase

import com.overklassniy.stankinschedule.schedule.parser.domain.model.CellBound
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParseResult
import com.overklassniy.stankinschedule.schedule.parser.domain.model.TimeCellBound
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PairExtractorTest {

    private val timeCells = listOf(
        TimeCellBound(startX = 0f, endX = 100f, startTime = "8:30", endTime = "10:05"),
        TimeCellBound(startX = 100f, endX = 200f, startTime = "10:15", endTime = "11:50"),
    )

    private fun cell(text: String, x: Float = 0f, w: Float = 100f) =
        CellBound(text = text, x = x, y = 0f, h = 10f, w = w, maxFontHeight = 10f)

    @Test
    fun `пустая ячейка возвращает пустой список`() {
        val result = PairExtractor().extractAllPairsFromCell(cell(""), timeCells)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `непарный текст возвращает Missing`() {
        val result = PairExtractor().extractAllPairsFromCell(cell("Кафедра"), timeCells)
        assertEquals(1, result.size)
        assertTrue(result[0] is ParseResult.Missing)
    }

    @Test
    fun `корректная запись пары с диапазоном дат парсится успешно`() {
        val result = PairExtractor()
            .extractAllPairsFromCell(
                cell("Информатика. Иванов И.И. лекции. 345. [01.09-15.12 к.н.]"),
                timeCells
            )
        assertEquals(1, result.size)
        val pair = (result[0] as? ParseResult.Success)?.pair
        assertTrue(pair != null)
    }

    @Test
    fun `несколько записей в ячейке разбираются по отдельности`() {
        val text = "Информатика. лекции. 345. [01.09-15.12 к.н.]" +
                "Физика. семинар. 200. [03.09]"
        val result = PairExtractor().extractAllPairsFromCell(cell(text), timeCells)
        assertEquals(2, result.size)
    }

    @Test
    fun `нулевая длительность слота возвращает ошибки Invalid time`() {
        val invertedTimeCells = listOf(
            TimeCellBound(startX = 0f, endX = 100f, startTime = "10:15", endTime = "10:05"),
        )
        val result = PairExtractor().extractAllPairsFromCell(
            cell("Информатика. лекции. 345. [01.09-15.12 к.н.]"),
            invertedTimeCells
        )
        assertEquals(1, result.size)
        assertTrue(result[0] is ParseResult.Error)
    }
}
