package com.overklassniy.stankinschedule.journal.core.domain.usecase.predict

import com.overklassniy.stankinschedule.journal.core.domain.model.Discipline
import com.overklassniy.stankinschedule.journal.core.domain.model.MarkType
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student

/**
 * Калькулятор для вычисления рейтинга и прогнозирования оценок студента.
 */
object PredictCalculater {

    /**
     * Вычисляет рейтинг для одной дисциплины на основе оценок и коэффициента.
     *
     * @param discipline Дисциплина с оценками и коэффициентом
     * @return Рейтинг дисциплины как произведение средневзвешенной оценки на коэффициент
     */
    private fun ratingForDiscipline(discipline: Discipline): Double {
        var disciplineSum = 0.0
        var disciplineCount = 0.0
        for (type in MarkType.entries) {
            discipline[type]?.let { mark ->
                disciplineSum += mark * type.weight
                disciplineCount += type.weight
            }
        }
        return (disciplineSum / disciplineCount) * discipline.factor
    }

    /**
     * Вычисляет общий рейтинг студента на основе оценок за семестр.
     *
     * @param marks Оценки за семестр
     * @return Общий рейтинг или 0.0, если рейтинг не может быть вычислен
     */
    fun computeRating(marks: SemesterMarks): Double {
        var ratingSum = 0.0
        var ratingCount = 0.0

        for (discipline in marks) {
            ratingSum += ratingForDiscipline(discipline)
            ratingCount += discipline.factor
        }

        val rating: Double = ratingSum / ratingCount
        return if (rating.isFinite()) rating else 0.0
    }

    /**
     * Вычисляет рейтинг студента на основе первого завершенного семестра.
     *
     * @param student Студент с информацией о семестрах
     * @param loadSemester Функция для загрузки оценок за семестр
     * @return Рейтинг студента или 0.0, если нет завершенных семестров
     */
    suspend fun rating(
        student: Student,
        loadSemester: suspend (semester: String) -> SemesterMarks,
    ): Double {
        for (semester in student.semesters) {
            val marks = loadSemester(semester)
            if (marks.isCompleted()) {
                return computeRating(marks)
            }
        }

        return 0.0
    }

    /**
     * Вычисляет сумму и количество оценок для дисциплины.
     *
     * @param discipline Дисциплина с оценками
     * @return Пара (сумма оценок, количество оценок)
     */
    private fun averageRatingForDiscipline(discipline: Discipline): Pair<Int, Int> {
        var disciplineSum = 0
        var disciplineCount = 0
        for ((_, mark) in discipline) {
            if (mark != Discipline.NO_MARK) {
                disciplineSum += mark
                disciplineCount++
            }
        }
        return disciplineSum to disciplineCount
    }

    /**
     * Вычисляет средний рейтинг на основе всех оценок за семестр.
     *
     * @param marks Оценки за семестр
     * @return Средний рейтинг
     */
    private fun averageRating(marks: SemesterMarks): Int {
        var ratingSum = 0
        var ratingCount = 0
        for (discipline in marks) {
            val (disciplineSum, disciplineCount) = averageRatingForDiscipline(discipline)
            ratingSum += disciplineSum
            ratingCount += disciplineCount
        }
        return ratingSum / ratingCount
    }

    /**
     * Вычисляет прогнозируемый рейтинг для дисциплины с учетом среднего рейтинга для отсутствующих оценок.
     *
     * @param discipline Дисциплина с оценками
     * @param averageRating Средний рейтинг для подстановки отсутствующих оценок
     * @return Прогнозируемый рейтинг дисциплины
     */
    private fun predictedRatingForDiscipline(discipline: Discipline, averageRating: Int): Double {
        var disciplineSum = 0.0
        var disciplineCount = 0.0
        for (type in MarkType.entries) {
            discipline[type]?.let { mark ->
                disciplineSum += if (mark == Discipline.NO_MARK) {
                    averageRating * type.weight
                } else {
                    mark * type.weight
                }
                disciplineCount += type.weight
            }
        }
        return (disciplineSum / disciplineCount) * discipline.factor
    }

    /**
     * Вычисляет прогнозируемый рейтинг на основе оценок за семестр и среднего рейтинга.
     *
     * @param marks Оценки за семестр
     * @param averageRating Средний рейтинг для подстановки отсутствующих оценок
     * @return Прогнозируемый рейтинг или 0.0, если рейтинг не может быть вычислен
     */
    private fun predictedRating(marks: SemesterMarks, averageRating: Int): Double {
        var ratingSum = 0.0
        var ratingCount = 0.0
        for (discipline in marks) {
            ratingSum += predictedRatingForDiscipline(discipline, averageRating)
            ratingCount += discipline.factor
        }

        val rating: Double = ratingSum / ratingCount
        return if (rating.isFinite()) rating else 0.0
    }

    /**
     * Прогнозирует рейтинг студента на основе последнего семестра и накопленного рейтинга.
     *
     * @param student Студент с информацией о семестрах
     * @param loadSemester Функция для загрузки оценок за семестр
     * @return Прогнозируемый рейтинг или 0.0, если прогноз невозможен
     */
    suspend fun predictRating(
        student: Student,
        loadSemester: suspend (semester: String) -> SemesterMarks,
    ): Double {
        if (student.semesters.isEmpty()) return 0.0

        val lastSemester = student.semesters.first()
        val lastSemesterMarks = loadSemester(lastSemester)

        var accumulatedRating = 0
        for (i in 1 until student.semesters.size - 1) {
            val semester = student.semesters[i]
            val rating = loadSemester(semester).accumulatedRating
            if (rating != null) {
                accumulatedRating = rating
                break
            }
        }

        if (accumulatedRating == 0) {
            val average = averageRating(lastSemesterMarks)
            if (average == 0) return 0.0
            accumulatedRating = average
        }

        return predictedRating(lastSemesterMarks, accumulatedRating)
    }
}