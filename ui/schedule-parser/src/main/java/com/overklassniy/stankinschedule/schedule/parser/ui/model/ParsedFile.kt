package com.overklassniy.stankinschedule.schedule.parser.ui.model

import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParseResult
import com.overklassniy.stankinschedule.schedule.table.domain.model.ScheduleTable

/**
 * Результаты парсинга PDF-файла расписания.
 *
 * @property successResult Список успешно распознанных пар.
 * @property missingResult Список неполных распознаваний (частично отсутствуют данные).
 * @property errorResult Список ошибок распознавания.
 * @property table Табличное представление расписания для превью.
 */
class ParsedFile(
    val successResult: List<ParseResult.Success>,
    val missingResult: List<ParseResult.Missing>,
    val errorResult: List<ParseResult.Error>,
    val table: ScheduleTable
)