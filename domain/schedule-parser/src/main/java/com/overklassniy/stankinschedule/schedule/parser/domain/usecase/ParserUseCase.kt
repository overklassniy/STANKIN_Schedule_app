package com.overklassniy.stankinschedule.schedule.parser.domain.usecase

import android.graphics.Bitmap
import com.overklassniy.stankinschedule.schedule.core.domain.model.Time
import com.overklassniy.stankinschedule.schedule.parser.domain.model.CellBound
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParseResult
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParserSettings
import com.overklassniy.stankinschedule.schedule.parser.domain.model.TimeCellBound
import com.overklassniy.stankinschedule.schedule.parser.domain.repository.PDFRepository
import javax.inject.Inject

/**
 * Use case для парсинга PDF файлов с расписанием.
 *
 * Отвечает за координацию процесса парсинга: получение данных из PDF через репозиторий,
 * обнаружение временных ячеек и извлечение пар с помощью [PairExtractor].
 */
class ParserUseCase @Inject constructor(
    private val parser: PDFRepository
) {

    private val extractor = PairExtractor()

    /**
     * Парсит PDF файл и извлекает список пар.
     *
     * @param path Путь к файлу расписания.
     * @param settings Настройки парсера (год, порог чувствительности).
     * @return Список результатов парсинга ([ParseResult]), который может содержать успешные пары, ошибки или пропущенные данные.
     */
    suspend fun parsePDF(
        path: String,
        settings: ParserSettings
    ): List<ParseResult> {
        val details = parser.parsePDF(path, settings.parserThreshold)
        val timeCells = detectTimeCells(details.cells)

        extractor.dateYear = settings.scheduleYear
        val result = details.cells.flatMap { cell ->
            extractor.extractAllPairsFromCell(cell, timeCells)
        }

        return result
    }

    /**
     * Генерирует превью (изображение) первой страницы PDF файла.
     *
     * @param path Путь к файлу.
     * @return Bitmap изображения первой страницы.
     */
    suspend fun renderPreview(path: String): Bitmap {
        return parser.renderPDF(path)
    }

    /**
     * Определяет границы временных слотов (столбцов) на основе ключевых меток времени.
     *
     * Ищет координаты времени "8:30" и "10:20" для вычисления ширины столбца и начальной позиции.
     *
     * @param cells Список всех текстовых ячеек, найденных в PDF.
     * @return Список границ временных ячеек [TimeCellBound].
     * @throws IllegalArgumentException Если ключевые метки времени не найдены.
     */
    private fun detectTimeCells(cells: List<CellBound>): List<TimeCellBound> {
        var middleFirst = -1f
        var middleSecond = -1f

        for (cell in cells) {
            if (cell.text.contains("8:30")) {
                middleFirst = cell.x + cell.w / 2
            }
            if (cell.text.contains("10:20")) {
                middleSecond = cell.x + cell.w / 2
            }
        }

        if (middleFirst < 0 || middleSecond < 0) {
            throw IllegalArgumentException("Time not found")
        }

        val delta = middleSecond - middleFirst
        val startX = middleFirst - delta / 2

        return MutableList(size = Time.STARTS.size) { index ->
            TimeCellBound(
                startX = startX + delta * index,
                endX = startX + delta * (index + 1),
                startTime = Time.STARTS[index],
                endTime = Time.ENDS[index],
            )
        }
    }
}