package com.overklassniy.stankinschedule.schedule.parser.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.model.DateModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateRange
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateSingle
import com.overklassniy.stankinschedule.schedule.core.domain.model.Frequency
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.model.Time
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type
import com.overklassniy.stankinschedule.schedule.parser.domain.model.CellBound
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParseResult
import com.overklassniy.stankinschedule.schedule.parser.domain.model.TimeCellBound
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import kotlin.math.abs

class PairExtractor {

    private val pairRegex = Regex(pattern = ParserPatterns.Common)

    private val dateRangeRegex = Regex(pattern = ParserPatterns.DateRange)
    private val dateSingleRegex = Regex(pattern = ParserPatterns.DateSingle)
    private val splitRegex = Regex(pattern = ".*?]")

    var dateYear = LocalDate.now().year
    private val dateFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")


    fun extractAllPairsFromCell(
        cell: CellBound,
        timeCells: List<TimeCellBound>
    ): List<ParseResult> {
        val textPairs = splitRegex
            .findAll(cell.text)
            .map { it.value.trim() }
            .toList()

        if (textPairs.isEmpty()) {
            if (cell.text.isEmpty()) {
                return emptyList()
            }
            return listOf(ParseResult.Missing(cell.text))
        }

        val cellTime = detectTimeFromCell(cell.x, cell.x + cell.w, timeCells)
        if (cellTime.duration < 1) {
            return textPairs.map { textPair ->
                ParseResult.Error(error = "Invalid time: $cellTime", context = textPair)
            }
        }

        return textPairs.map { textPair ->
            try {
                ParseResult.Success(extractPairFromCell(textPair, cellTime))
            } catch (e: Exception) {
                ParseResult.Error(error = e.message ?: e.toString(), context = textPair)
            }
        }
    }

    private fun detectTimeFromCell(
        start: Float,
        end: Float,
        timeCells: List<TimeCellBound>
    ): Time {
        var startDelta = 100_000f
        var startTime = ""

        var endDelta = 100_000f
        var endTime = ""

        for (timeCell in timeCells) {
            if (abs(start - timeCell.startX) < startDelta) {
                startDelta = abs(start - timeCell.startX)
                startTime = timeCell.startTime
            }

            if (abs(end - timeCell.endX) < endDelta) {
                endDelta = abs(end - timeCell.endX)
                endTime = timeCell.endTime
            }
        }

        return Time(startTime, endTime)
    }

    private fun extractPairFromCell(data: String, time: Time): PairModel {
        val pairMatch = pairRegex.matchEntire(data)
            ?: throw IllegalArgumentException("Pair not found: '$data'")

        return PairModel(
            title = extractTitle(pairMatch.groupValues[1]),
            lecturer = extractLecturer(pairMatch.groupValues[2]),
            type = extractType(pairMatch.groupValues[4]),
            subgroup = extractSubgroup(pairMatch.groupValues[6]),
            classroom = extractClassroom(pairMatch.groupValues[7]),
            time = time,
            date = extractDate(pairMatch.groupValues[8])
        )
    }

    private fun extractTitle(title: String): String {
        return title.dropLast(1).trim()
    }

    private fun extractLecturer(lecturer: String): String {
        if (lecturer.isEmpty()) return ""
        return if (lecturer.endsWith('.')) {
            lecturer.dropLast(1).trim()
        } else {
            lecturer.trim()
        }
    }

    private fun extractClassroom(classroom: String): String {
        if (classroom.isEmpty()) return ""
        return classroom.dropLast(1).trim()
    }

    private fun extractType(type: String): Type {
        return when (type.dropLast(1).trim().lowercase()) {
            "семинар" -> Type.SEMINAR
            "лекции", "лекция" -> Type.LECTURE
            "лабораторные занятия", "лабораторная" -> Type.LABORATORY
            "" -> Type.LECTURE
            else -> throw IllegalArgumentException("Unknown type: '$type'")
        }
    }

    private fun extractSubgroup(subgroup: String): Subgroup {
        if (subgroup.isEmpty()) {
            return Subgroup.COMMON
        }

        return when (subgroup.dropLast(1).trim().uppercase()) {
            "(А)" -> Subgroup.A
            "(Б)" -> Subgroup.B
            else -> throw IllegalArgumentException("Unknown subgroup: '$subgroup'")
        }
    }

    private fun extractDate(dateString: String): DateModel {
        val date = DateModel()
        val textDates = dateString
            .replace("[", "")
            .replace("]", "")
            .trim()
            .lowercase()
            .split(',')

        for (textDate in textDates) {
            // range
            val matchRange = dateRangeRegex.matchEntire(textDate)
            if (matchRange != null) {
                val frequency = when (val frequencyText = matchRange.groupValues[3]) {
                    "к.н." -> Frequency.EVERY
                    "ч.н." -> Frequency.THROUGHOUT
                    else -> throw IllegalArgumentException("Unknown frequency: '$frequencyText'")
                }

                val item = DateRange(
                    firstDate = dateConvertor(matchRange.groupValues[1]),
                    secondDate = dateConvertor(matchRange.groupValues[2]),
                    frequencyDate = frequency
                )
                date.add(item)

                continue
            }
            // single
            val matchSingle = dateSingleRegex.matchEntire(textDate)
            if (matchSingle != null) {

                val item = DateSingle(
                    date = dateConvertor(matchSingle.groupValues[1])
                )
                date.add(item)

                continue
            }

            throw IllegalArgumentException("Unknown date: '$textDate'")
        }


        return date
    }

    private fun dateConvertor(date: String): LocalDate {
        val parsedDate = dateFormatter.parseLocalDate("$date.$dateYear")
        val now = LocalDate.now()

        val previousYearDate = dateFormatter.parseLocalDate("$date.${dateYear - 1}")
        if (abs(daysBetween(now, parsedDate)) > abs(daysBetween(now, previousYearDate))) {
            if (previousYearDate.dayOfWeek != DateTimeConstants.SUNDAY) {
                return previousYearDate
            }
        }

        if (parsedDate.dayOfWeek == DateTimeConstants.SUNDAY) {
            if (previousYearDate.dayOfWeek != DateTimeConstants.SUNDAY) {
                return previousYearDate
            }
        }
        return parsedDate
    }

    private fun daysBetween(d1: LocalDate, d2: LocalDate): Int {
        return org.joda.time.Days.daysBetween(if (d1 < d2) d1 else d2, if (d1 < d2) d2 else d1).days
    }

    object ParserPatterns {

        const val Title = "([а-яА-ЯёЁa-zA-Z0-9\\.\\s\\,\\-\\(\\)\\/\\:]+?\\.)"
        const val Lecturer = "([а-яА-ЯёЁae\\s\\_]+\\s([а-яА-я]\\.?){1,2})?"
        const val Type = "((лабораторные занятия|Лабораторные занятия|Лабораторная|семинар|Семинар|лекции|Лекции|лекция|Лекция)\\.|(?<=\\s)\\.)"
        const val Subgroup = "(\\([абАБ]\\)\\.)?"
        const val Classroom = "([^\\[\\]]+?\\.)"
        const val Date =
            "(\\[((\\,)|(\\s?(\\d{2}\\.\\d{2})\\-(\\d{2}\\.\\d{2})\\s*?([чкЧК]\\.[нН]\\.{1,2})|(\\s?(\\d{2}\\.\\d{2}))))+\\])"
        const val DateRange = "\\s?(\\d{2}\\.\\d{2})-(\\d{2}\\.\\d{2})\\s*?([чк]\\.[н]\\.)"
        const val DateSingle = "\\s?(\\d{2}\\.\\d{2})"

        val Common = listOf(Title, Lecturer, Type, Subgroup, Classroom, Date)
            .joinToString("\\s?")
    }
}