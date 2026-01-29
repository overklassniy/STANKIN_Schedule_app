package com.overklassniy.stankinschedule.schedule.parser.domain.model

/**
 * Детали парсинга PDF файла (промежуточный результат).
 *
 * @property cells Список ячеек с текстом и их координатами, извлеченных из PDF.
 */
class ParseDetail(
    val cells: List<CellBound>
)