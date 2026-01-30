package com.overklassniy.stankinschedule.schedule.editor.ui.components

import org.joda.time.LocalDate

/**
 * Результат выбора даты в диалоге.
 *
 * @property id Идентификатор запроса даты, используется для сопоставления результата.
 * @property date Выбранная дата [LocalDate].
 */
class DateResult(
    val id: String,
    val date: LocalDate
)