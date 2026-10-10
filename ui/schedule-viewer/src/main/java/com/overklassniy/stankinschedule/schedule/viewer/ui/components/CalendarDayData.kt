package com.overklassniy.stankinschedule.schedule.viewer.ui.components

import com.overklassniy.stankinschedule.schedule.core.domain.model.Type

/**
 * Данные о парах дня для отображения в календаре.
 *
 * @property pairTypes Список типов пар в этот день (для отрисовки точек).
 * @property isDayOff Признак выходного дня по данным isdayoff.ru.
 */
data class CalendarDayData(
    val pairTypes: List<Type> = emptyList(),
    val isDayOff: Boolean = false,
)
