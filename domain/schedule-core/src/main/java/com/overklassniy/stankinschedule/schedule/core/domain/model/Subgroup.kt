package com.overklassniy.stankinschedule.schedule.core.domain.model

/**
 * Перечисление подгрупп.
 *
 * @property tag Строковое представление подгруппы.
 */
enum class Subgroup(val tag: String) {

    /** Подгруппа А. */
    A("A"),

    /** Подгруппа Б. */
    B("B"),

    /** Общая для всех (без подгруппы). */
    COMMON("Common");

    /**
     * Проверяет пересечение подгрупп.
     *
     * Пересечение есть, если подгруппы совпадают или одна из них - COMMON.
     *
     * @param subgroup Другая подгруппа.
     * @return true, если есть пересечение.
     */
    fun isIntersect(subgroup: Subgroup): Boolean {
        return this == subgroup || this == COMMON || subgroup == COMMON
    }

    /**
     * Нужно ли отображать подгруппу в интерфейсе.
     *
     * @return true, если подгруппа не COMMON.
     */
    fun isShow(): Boolean {
        return this != COMMON
    }

    /**
     * Возвращает строковое представление подгруппы.
     */
    override fun toString(): String {
        return tag
    }

    companion object {

        /**
         * Получает подгруппу по строковому значению.
         *
         * @param value Строка (например, "A" или "Common").
         * @return [Subgroup].
         * @throws IllegalArgumentException Если подгруппа не найдена.
         */
        @JvmStatic
        fun of(value: String): Subgroup {
            for (subgroup in entries) {
                if (subgroup.tag.equals(value, ignoreCase = true)) {
                    return subgroup
                }
            }
            throw IllegalArgumentException("No parse subgroup: $value")
        }
    }
}