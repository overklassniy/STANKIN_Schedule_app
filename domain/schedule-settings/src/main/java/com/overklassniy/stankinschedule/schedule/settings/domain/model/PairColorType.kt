package com.overklassniy.stankinschedule.schedule.settings.domain.model

/**
 * Типы пар и их цвета по умолчанию.
 *
 * @property hex Цвет по умолчанию в формате HEX.
 */
enum class PairColorType(val hex: String) {
    /** Цвет для лекции */
    Lecture("#80DEEA"),

    /** Цвет для семинара */
    Seminar("#FFF59D"),

    /** Цвет для лабораторной работы */
    Laboratory("#C5E1A5"),

    /** Цвет для подгруппы A */
    SubgroupA("#FFCC80"),

    /** Цвет для подгруппы B */
    SubgroupB("#D1C4E9");
}