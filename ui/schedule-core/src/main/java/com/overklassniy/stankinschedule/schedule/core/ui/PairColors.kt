package com.overklassniy.stankinschedule.schedule.core.ui

import androidx.compose.ui.graphics.Color
import com.overklassniy.stankinschedule.core.ui.ext.parse
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup

/**
 * Цветовая схема для отображения типов пар и подгрупп.
 *
 * @property lectureColor Цвет метки для лекций.
 * @property seminarColor Цвет метки для семинаров.
 * @property laboratoryColor Цвет метки для лабораторных.
 * @property subgroupAColor Цвет метки для подгруппы А.
 * @property subgroupBColor Цвет метки для подгруппы Б.
 */
class PairColors(
    val lectureColor: Color,
    val seminarColor: Color,
    val laboratoryColor: Color,
    val subgroupAColor: Color,
    val subgroupBColor: Color
)

/**
 * Конвертирует модель настроек цветов пар [PairColorGroup] в объект [PairColors].
 *
 * Примечания:
 * - Строковые цвета должны быть в формате HEX (#RRGGBB или #AARRGGBB).
 *
 * @return Сформированная палитра [PairColors] для UI.
 */
fun PairColorGroup.toColor(): PairColors {
    return PairColors(
        lectureColor = Color.parse(lectureColor),
        seminarColor = Color.parse(seminarColor),
        laboratoryColor = Color.parse(laboratoryColor),
        subgroupAColor = Color.parse(subgroupAColor),
        subgroupBColor = Color.parse(subgroupBColor)
    )
}