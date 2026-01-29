package com.overklassniy.stankinschedule.schedule.repository.domain.model

/**
 * Перечисление курсов обучения.
 *
 * @property number Номер курса.
 */
enum class Course(
    val number: Int,
) {
    /** Первый курс */
    First(1),

    /** Второй курс */
    Second(2),

    /** Третий курс */
    Three(3),

    /** Четвертый курс */
    Four(4),

    /** Пятый курс */
    Five(5);
}