package com.overklassniy.stankinschedule.journal.core.domain.model

/**
 * Типы оценок (контрольных точек) в журнале.
 *
 * @property tag Строковое обозначение типа оценки в системе (например, "М1", "Э").
 * @property weight Вес оценки (не используется в текущей логике, возможно, задел на будущее).
 */
enum class MarkType(val tag: String, val weight: Int) {

    /** Первый модуль. */
    FIRST_MODULE("М1", 3),

    /** Второй модуль. */
    SECOND_MODULE("М2", 2),

    /** Курсовая работа. */
    COURSEWORK("К", 5),

    /** Зачет. */
    CREDIT("З", 5),

    /** Экзамен. */
    EXAM("Э", 7);

    override fun toString(): String {
        return tag
    }

    companion object {

        /**
         * Получает тип оценки по его строковому представлению.
         *
         * @param value Строковое обозначение типа оценки.
         * @return Соответствующий [MarkType].
         * @throws IllegalArgumentException Если тип оценки неизвестен.
         */
        @JvmStatic
        fun of(value: String): MarkType {
            for (type in entries) {
                if (type.tag == value) {
                    return type
                }
            }

            // Обработка пустого значения как экзамена (возможно, особенность парсинга)
            if (value.isEmpty()) {
                return EXAM
            }

            throw IllegalArgumentException("Unknown mark type: '$value'")
        }
    }
}