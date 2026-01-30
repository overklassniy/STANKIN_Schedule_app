package com.overklassniy.stankinschedule.schedule.editor.ui.components

import androidx.annotation.StringRes
import org.joda.time.LocalDate

/**
 * Запрос на выбор даты в диалоге календаря.
 *
 * Используется для передачи заголовка диалога, текущей выбранной даты и идентификатора,
 * по которому сопоставляется результат выбора.
 *
 * @property title Идентификатор строкового ресурса заголовка диалога.
 * @property selectedDate Текущая выбранная дата, которая будет показана в календаре.
 * @property id Уникальный идентификатор запроса для сопоставления результата.
 */
class DateRequest(
    @param:StringRes val title: Int,
    val selectedDate: LocalDate,
    val id: String,
)