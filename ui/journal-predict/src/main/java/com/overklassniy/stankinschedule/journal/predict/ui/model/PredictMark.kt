package com.overklassniy.stankinschedule.journal.predict.ui.model

import com.overklassniy.stankinschedule.journal.core.domain.model.MarkType

/**
 * Модель оценки для предсказания рейтинга.
 *
 * @property discipline Название дисциплины.
 * @property type Тип оценки [MarkType].
 * @property isExposed Отображать ли оценку в UI.
 * @property value Текущее значение оценки (0, если пусто).
 */
class PredictMark(
    val discipline: String,
    val type: MarkType,
    val isExposed: Boolean,
    var value: Int,
)