package com.overklassniy.stankinschedule.journal.core.domain.model

import com.google.gson.annotations.SerializedName
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks.Companion.ACCUMULATED_RATING
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks.Companion.RATING

/**
 * Модель оценок за семестр.
 *
 * Содержит список дисциплин и общий рейтинг студента за семестр.
 *
 * @property disciplines Список дисциплин [Discipline] с оценками.
 * @property _rating Рейтинг за текущий семестр.
 * @property _accumulatedRating Накопленный рейтинг за все время обучения.
 */
data class SemesterMarks(
    @SerializedName("disciplines")
    private val disciplines: ArrayList<Discipline> = arrayListOf(),
    @SerializedName("rating")
    private var _rating: Int? = null,
    @SerializedName("accumulatedRating")
    private var _accumulatedRating: Int? = null,
) : Iterable<Discipline> {

    /** Рейтинг за семестр (доступ только для чтения). */
    val rating: Int? get() = _rating

    /** Накопленный рейтинг (доступ только для чтения). */
    val accumulatedRating: Int? get() = _accumulatedRating

    /**
     * Добавляет или обновляет оценку по дисциплине.
     *
     * Если название дисциплины соответствует [RATING] или [ACCUMULATED_RATING],
     * обновляются соответствующие поля рейтинга.
     * Иначе ищется дисциплина в списке, и обновляется её оценка. Если дисциплина не найдена, создается новая.
     *
     * @param disciplineTitle Название дисциплины.
     * @param type Тип оценки (строковое представление).
     * @param value Значение оценки (баллы).
     * @param factor Коэффициент дисциплины.
     */
    fun addMark(disciplineTitle: String, type: String, value: Int, factor: Double) {
        if (disciplineTitle == RATING) {
            _rating = value
            return
        }
        if (disciplineTitle == ACCUMULATED_RATING) {
            _accumulatedRating = value
            return
        }

        // Пропуск специфичных случаев
        if (disciplineTitle == "Государственный экзамен" && type.trim().isEmpty()) {
            return
        }

        val markType = MarkType.of(type)
        for (discipline in disciplines) {
            if (discipline.title == disciplineTitle) {
                discipline[markType] = value
                return
            }
        }

        val discipline = Discipline(disciplineTitle, linkedMapOf(Pair(markType, value)), factor)
        disciplines.add(discipline)
        // Сортировка дисциплин по названию
        disciplines.sortWith { o1, o2 -> o1.title.compareTo(o2.title) }
    }

    /**
     * Обновляет конкретную оценку в существующей дисциплине.
     *
     * @param disciplineName Название дисциплины.
     * @param type Тип оценки [MarkType].
     * @param mark Новое значение оценки.
     */
    fun updateMark(disciplineName: String, type: MarkType, mark: Int) {
        for (discipline in disciplines) {
            if (discipline.title == disciplineName) {
                discipline[type] = mark
                break
            }
        }
    }

    /**
     * Проверяет, завершен ли семестр (выставлены ли все оценки по всем дисциплинам).
     *
     * @return `true`, если все дисциплины завершены, иначе `false`.
     */
    fun isCompleted(): Boolean {
        for (disciple in disciplines) {
            if (!disciple.isCompleted()) {
                return false
            }
        }
        return true
    }

    /**
     * Возвращает итератор по дисциплинам семестра.
     *
     * @return Итератор объектов [Discipline].
     */
    override fun iterator(): Iterator<Discipline> = disciplines.iterator()

    companion object {
        /** Ключевое слово для определения рейтинга в данных. */
        const val RATING = "Рейтинг"

        /** Ключевое слово для определения накопленного рейтинга в данных. */
        const val ACCUMULATED_RATING = "Накопленный Рейтинг"
    }
}