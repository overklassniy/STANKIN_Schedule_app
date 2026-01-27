package com.overklassniy.stankinschedule.journal.core.data.mapper

import com.overklassniy.stankinschedule.journal.core.data.model.MarkResponse
import com.overklassniy.stankinschedule.journal.core.data.model.SemestersResponse
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student

/**
 * Преобразует ответ сервера со списком семестров в доменную модель студента.
 *
 * Алгоритм:
 * 1. Формирует полное имя из фамилии и инициалов.
 * 2. Копирует название группы.
 * 3. Переворачивает список семестров (от новых к старым).
 *
 * @return Объект [Student] с данными о студенте и списком семестров
 */
fun SemestersResponse.toStudent(): Student {
    return Student(
        name = "$surname $initials",
        group = group,
        semesters = semesters.reversed()
    )
}

/**
 * Преобразует список ответов с оценками в доменную модель оценок за семестр.
 *
 * Алгоритм:
 * 1. Создает пустой объект [SemesterMarks].
 * 2. Проходит по списку [MarkResponse] и добавляет каждую оценку в объект.
 *    При добавлении учитывается название предмета, тип оценки, значение и коэффициент.
 *
 * @return Объект [SemesterMarks], содержащий сгруппированные оценки
 */
fun List<MarkResponse>.toSemesterMarks(): SemesterMarks {
    return SemesterMarks().apply {
        this@toSemesterMarks.forEach { mark ->
            addMark(mark.title, mark.type, mark.value, mark.factor)
        }
    }
}