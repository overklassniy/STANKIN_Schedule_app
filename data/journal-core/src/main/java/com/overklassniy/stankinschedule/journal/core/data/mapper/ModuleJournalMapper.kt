package com.overklassniy.stankinschedule.journal.core.data.mapper

import com.overklassniy.stankinschedule.journal.core.data.model.AllowedSemesterResponse
import com.overklassniy.stankinschedule.journal.core.data.model.DisciplineResponse
import com.overklassniy.stankinschedule.journal.core.data.model.ExternalStudyProgressResponse
import com.overklassniy.stankinschedule.journal.core.data.model.PeriodResponse
import com.overklassniy.stankinschedule.journal.core.domain.model.Discipline
import com.overklassniy.stankinschedule.journal.core.domain.model.JournalSemester
import com.overklassniy.stankinschedule.journal.core.domain.model.MarkType
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import kotlin.math.roundToInt

/**
 * Преобразует идентификатор семестра из ответа API в доменную модель.
 *
 * @return Объект [JournalSemester].
 */
fun AllowedSemesterResponse.toJournalSemester(): JournalSemester {
    return JournalSemester(course = course, semester = semester)
}

/**
 * Извлекает отсортированный список доступных семестров из ответа API.
 *
 * @return Список [JournalSemester] по убыванию (от новых к старым).
 */
fun ExternalStudyProgressResponse.toSemesters(): List<JournalSemester> {
    return allowedSemesters
        .map { it.toJournalSemester() }
        .sortedDescending()
}

/**
 * Преобразует дисциплину из ответа API в доменную модель.
 *
 * Отсутствующие (null) оценки не добавляются в словарь оценок —
 * контрольная точка по такому типу считается неприменимой.
 *
 * @return Объект [Discipline].
 */
fun DisciplineResponse.toDiscipline(): Discipline {
    val marks = linkedMapOf<MarkType, Int>()
    module1?.let { marks[MarkType.FIRST_MODULE] = it }
    module2?.let { marks[MarkType.SECOND_MODULE] = it }
    coursework?.let { marks[MarkType.COURSEWORK] = it }
    credit?.let { marks[MarkType.CREDIT] = it }
    exam?.let { marks[MarkType.EXAM] = it }

    return Discipline(
        title = disciplineName,
        marks = marks,
        factor = coefficient ?: Discipline.NO_FACTOR
    )
}

/**
 * Преобразует данные периода из ответа API в доменную модель оценок за семестр.
 *
 * @return Объект [SemesterMarks] с дисциплинами и рейтингами;
 * для отсутствующего периода — пустой объект.
 */
fun PeriodResponse?.toSemesterMarks(): SemesterMarks {
    if (this == null) return SemesterMarks()

    val marks = SemesterMarks(
        disciplines = ArrayList(disciplines.map { it.toDiscipline() })
    )

    rating?.let { marks.addMark(SemesterMarks.RATING, "", it.roundToInt(), 0.0) }
    accumulatedRating?.let { marks.addMark(SemesterMarks.ACCUMULATED_RATING, "", it.roundToInt(), 0.0) }

    return marks
}
