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

/**
 * Класс для извлечения информации о парах из текстовых данных.
 *
 * Использует регулярные выражения для разбора строк расписания и преобразования их в объекты [PairModel].
 */
class PairExtractor {
    private val pairRegex = Regex(pattern = ParserPatterns.COMMON)
    private val dateRangeRegex = Regex(pattern = ParserPatterns.DATE_RANGE)
    private val dateSingleRegex = Regex(pattern = ParserPatterns.DATE_SINGLE)
    private val splitRegex = Regex(pattern = ".*?]")
    var dateYear = LocalDate.now().year
    private val dateFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")

    /**
     * Извлекает все пары из текстовой ячейки.
     *
     * Разбивает текст ячейки на отдельные записи пар, определяет время для каждой пары
     * и парсит детали (название, преподаватель, аудитория и т.д.).
     *
     * @param cell Ячейка с текстом [CellBound].
     * @param timeCells Список ячеек времени для определения временных интервалов.
     * @return Список результатов парсинга [ParseResult] (успешные, ошибки, пропущенные).
     */
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

    /**
     * Определяет время начала и окончания пары по координатам ячейки.
     *
     * Сопоставляет координаты текстовой ячейки с известными координатами столбцов времени.
     *
     * @param start Координата X начала ячейки.
     * @param end Координата X конца ячейки.
     * @param timeCells Список границ столбцов времени.
     * @return Объект [Time] с найденным временем начала и окончания.
     */
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

    /**
     * Извлекает данные пары из строки текста с помощью регулярных выражений.
     *
     * @param data Строка с полным описанием пары.
     * @param time Объект времени пары.
     * @return Модель пары [PairModel].
     * @throws IllegalArgumentException Если формат строки не соответствует ожидаемому.
     */
    private fun extractPairFromCell(data: String, time: Time): PairModel {
        val pairMatch = pairRegex.matchEntire(data)
            ?: throw IllegalArgumentException("Pair not found: '$data'")

        return PairModel(
            title = extractTitle(pairMatch.groupValues[1]),
            lecturer = extractLecturer(pairMatch.groupValues[2]),
            type = extractType(pairMatch.groupValues[4]),
            subgroup = extractSubgroup(pairMatch.groupValues[5]),
            classroom = extractClassroom(pairMatch.groupValues[6]),
            time = time,
            date = extractDate(pairMatch.groupValues[7])
        )
    }

    /**
     * Извлекает название предмета из группы захвата regex.
     */
    private fun extractTitle(title: String): String {
        return title.dropLast(1).trim()
    }

    /**
     * Извлекает имя преподавателя.
     * Добавляет точку в конце, если она отсутствует.
     */
    private fun extractLecturer(lecturer: String): String {
        if (lecturer.isEmpty()) return ""
        val trimmed = lecturer.trim()
        return if (trimmed.endsWith('.')) {
            trimmed
        } else {
            "$trimmed."
        }
    }

    /**
     * Извлекает аудиторию.
     */
    private fun extractClassroom(classroom: String): String {
        if (classroom.isEmpty()) return ""
        return classroom.dropLast(1).trim()
    }

    /**
     * Определяет тип занятия (лекция, семинар, лаб. работа).
     */
    private fun extractType(type: String): Type {
        val normalized = type.trim().removeSuffix(".").lowercase()
        return when (normalized) {
            "семинар" -> Type.SEMINAR
            "лекции", "лекция" -> Type.LECTURE
            "лабораторные занятия", "лабораторная" -> Type.LABORATORY
            "" -> Type.LECTURE
            else -> throw IllegalArgumentException("Unknown type: '$type'")
        }
    }

    /**
     * Определяет подгруппу (А, Б или общая).
     */
    private fun extractSubgroup(subgroup: String): Subgroup {
        if (subgroup.isEmpty()) {
            return Subgroup.COMMON
        }

        val normalized = subgroup.dropLast(1).trim().uppercase()
            .replace("(A)", "(А)")
            .replace("(B)", "(Б)")
        return when (normalized) {
            "(А)" -> Subgroup.A
            "(Б)" -> Subgroup.B
            else -> throw IllegalArgumentException("Unknown subgroup: '$subgroup'")
        }
    }

    /**
     * Парсит строку с датами проведения занятий.
     * Поддерживает одиночные даты и диапазоны с периодичностью.
     */
    private fun extractDate(dateString: String): DateModel {
        val date = DateModel()
        val textDates = dateString
            .replace("[", "")
            .replace("]", "")
            .trim()
            .lowercase()
            .split(',')

        for (textDate in textDates) {
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

    /**
     * Конвертирует строку даты в [LocalDate].
     * Учитывает переход через год (для осеннего семестра).
     */
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

    /**
     * Вычисляет количество дней между двумя датами.
     */
    private fun daysBetween(d1: LocalDate, d2: LocalDate): Int {
        return org.joda.time.Days.daysBetween(if (d1 < d2) d1 else d2, if (d1 < d2) d2 else d1).days
    }

    object ParserPatterns {
        const val TITLE = "([а-яА-ЯёЁa-zA-Z0-9\\.\\s\\,\\-\\(\\)\\/\\:]+?\\.)"
        const val LECTURER = "([а-яА-ЯёЁa-zA-Z0-9 \\t\\_\\.]+?\\s*)?"
        const val TYPE =
            "((лабораторные занятия|Лабораторные занятия|Лабораторная|семинар|Семинар|лекции|Лекции|лекция|Лекция)\\.|(?<=\\s)\\.)"
        const val SUBGROUP = "(\\([абАБaAbB]\\)\\.)?"
        const val CLASSROOM = "([^\\[\\]]+?\\.)"
        const val DATE =
            "(\\[(?:(?:\\,)|(?:\\s?\\d{2}\\.\\d{2}\\-\\d{2}\\.\\d{2}\\s*?[чкЧК]\\.[нН]\\.{1,2})|(?:\\s?\\d{2}\\.\\d{2}))+\\])"
        const val DATE_RANGE = "\\s?(\\d{2}\\.\\d{2})-(\\d{2}\\.\\d{2})\\s*?([чк]\\.[н]\\.)"
        const val DATE_SINGLE = "\\s?(\\d{2}\\.\\d{2})"
        val COMMON = listOf(TITLE, LECTURER, TYPE, SUBGROUP, CLASSROOM, DATE)
            .joinToString("\\s?")
    }
}