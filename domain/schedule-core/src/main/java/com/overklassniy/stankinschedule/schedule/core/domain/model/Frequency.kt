package com.overklassniy.stankinschedule.schedule.core.domain.model

/**
 * Перечисление периодичности занятий.
 *
 * @property tag Строковое представление периодичности (используется при парсинге).
 * @property period Период в днях.
 */
enum class Frequency(val tag: String, val period: Int) {
    /** Занятие проходит один раз. */
    ONCE("once", 1),

    /** Занятие проходит каждую неделю (каждые 7 дней). */
    EVERY("every", 7),

    /** Занятие проходит через неделю (каждые 14 дней). */
    THROUGHOUT("throughout", 14);

    companion object {

        /**
         * Получает периодичность по строковому тегу.
         *
         * @param value Строковый тег.
         * @return [Frequency] соответствующая тегу.
         * @throws IllegalArgumentException Если тег не найден.
         */
        fun of(value: String): Frequency {
            for (frequency in entries) {
                if (frequency.tag == value) {
                    return frequency
                }
            }
            throw IllegalArgumentException("No parse frequency: $value")
        }
    }
}