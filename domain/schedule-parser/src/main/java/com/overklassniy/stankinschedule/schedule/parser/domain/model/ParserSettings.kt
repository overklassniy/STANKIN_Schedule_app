package com.overklassniy.stankinschedule.schedule.parser.domain.model

/**
 * Настройки парсера расписания.
 *
 * @property scheduleYear Год расписания (используется для корректного определения дат).
 * @property parserThreshold Порог чувствительности парсера (например, для объединения близко расположенных блоков).
 */
data class ParserSettings(
    val scheduleYear: Int,
    val parserThreshold: Float
)