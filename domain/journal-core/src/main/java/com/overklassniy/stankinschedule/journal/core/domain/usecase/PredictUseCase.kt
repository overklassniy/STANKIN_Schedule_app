package com.overklassniy.stankinschedule.journal.core.domain.usecase

import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalRepository
import com.overklassniy.stankinschedule.journal.core.domain.usecase.predict.PredictCalculater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * UseCase для расчета и прогнозирования рейтинга студента.
 *
 * Позволяет вычислять текущий рейтинг, прогнозируемый рейтинг (с учетом текущих оценок)
 * и рейтинг за конкретный семестр.
 */
class PredictUseCase @Inject constructor(
    private val journal: JournalRepository,
) {
    /**
     * Вычисляет текущий официальный рейтинг студента.
     *
     * @param student Объект студента.
     * @return [Flow] со строковым представлением рейтинга (например, "45.50" или "--.--").
     */
    fun rating(student: Student): Flow<String> = flow {
        val rating = PredictCalculater.rating(student) { journal.semesterMarks(it) }
        if (rating.isFinite() && rating > 0.0) {
            emit("%.2f".format(rating))
        } else {
            emit("--.--")
        }
    }.flowOn(Dispatchers.Default)

    /**
     * Прогнозирует возможный рейтинг студента.
     *
     * Использует текущие оценки для предсказания итогового рейтинга.
     *
     * @param student Объект студента.
     * @return [Flow] со строковым представлением прогнозируемого рейтинга.
     */
    fun predictRating(student: Student): Flow<String> = flow {
        val rating = PredictCalculater.predictRating(student) { journal.semesterMarks(it) }
        if (rating.isFinite() && rating > 0.0) {
            emit("%.2f".format(rating))
        } else {
            emit("--.--")
        }
    }.flowOn(Dispatchers.Default)

    /**
     * Вычисляет рейтинг за семестр на основе переданных оценок.
     *
     * @param marks Объект [SemesterMarks] с оценками.
     * @return Вычисленный рейтинг (Double).
     */
    fun predictSemester(marks: SemesterMarks): Double {
        return PredictCalculater.computeRating(marks)
    }

    /**
     * Загружает оценки за указанный семестр.
     *
     * @param semester Название семестра.
     * @return [Flow] с объектом [SemesterMarks].
     */
    fun semesterMarks(semester: String): Flow<SemesterMarks> = flow {
        val semesterMarks = journal.semesterMarks(semester)
        emit(semesterMarks)
    }.flowOn(Dispatchers.IO)

    /**
     * Получает список семестров студента.
     *
     * @return [Flow] со списком названий семестров.
     */
    fun semesters(): Flow<List<String>> = flow {
        val student = journal.student()
        emit(student.semesters)
    }.flowOn(Dispatchers.IO)
}