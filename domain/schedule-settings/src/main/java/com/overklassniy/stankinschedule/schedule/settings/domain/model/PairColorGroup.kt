package com.overklassniy.stankinschedule.schedule.settings.domain.model

/**
 * Группа пользовательских цветов для типов пар.
 *
 * @property lectureColor Цвет лекции.
 * @property seminarColor Цвет семинара.
 * @property laboratoryColor Цвет лабораторной работы.
 * @property subgroupAColor Цвет подгруппы A.
 * @property subgroupBColor Цвет подгруппы B.
 */
data class PairColorGroup(
    val lectureColor: String,
    val seminarColor: String,
    val laboratoryColor: String,
    val subgroupAColor: String,
    val subgroupBColor: String
) {
    companion object {
        /**
         * Возвращает группу цветов по умолчанию на основе значений [PairColorType].
         *
         * @return Группа цветов по умолчанию.
         */
        fun default() = PairColorGroup(
            lectureColor = PairColorType.Lecture.hex,
            seminarColor = PairColorType.Seminar.hex,
            laboratoryColor = PairColorType.Laboratory.hex,
            subgroupAColor = PairColorType.SubgroupA.hex,
            subgroupBColor = PairColorType.SubgroupB.hex
        )
    }
}