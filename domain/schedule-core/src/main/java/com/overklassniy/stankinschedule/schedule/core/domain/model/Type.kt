package com.overklassniy.stankinschedule.schedule.core.domain.model

/**
 * Перечисление типов занятий.
 *
 * @property tag Строковое представление типа.
 */
enum class Type(val tag: String) {
    /** Лекция */
    LECTURE("Lecture"),

    /** Семинар */
    SEMINAR("Seminar"),

    /** Лабораторная работа */
    LABORATORY("Laboratory");

    companion object {

        /**
         * Получает тип занятия по строковому значению.
         *
         * @param value Строка типа (например, "Lecture").
         * @return [Type].
         * @throws IllegalArgumentException Если тип не найден.
         */
        fun of(value: String): Type {
            for (type in entries) {
                if (type.tag.equals(value, ignoreCase = true)) {
                    return type
                }
            }
            throw IllegalArgumentException("No parse type: $value")
        }
    }
}