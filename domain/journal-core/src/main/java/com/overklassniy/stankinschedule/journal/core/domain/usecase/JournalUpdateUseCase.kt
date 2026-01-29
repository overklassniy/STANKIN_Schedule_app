package com.overklassniy.stankinschedule.journal.core.domain.usecase

import com.overklassniy.stankinschedule.core.domain.ext.subHours
import com.overklassniy.stankinschedule.journal.core.domain.model.MarkType
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalStorageRepository
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * UseCase для фонового обновления данных журнала.
 *
 * Используется в Worker'ах для периодической проверки новых семестров и изменений в оценках.
 */
class JournalUpdateUseCase @Inject constructor(
    private val journal: JournalRepository,
    private val storage: JournalStorageRepository
) {

    /**
     * Проверяет появление новых семестров у студента.
     *
     * Сравнивает текущий кэшированный список семестров с актуальным списком с сервера.
     * Если с момента последнего кэширования прошло меньше часа, обновление не выполняется.
     *
     * @return Пара:
     * 1. [Student]? - обновленные данные студента (или null, если обновление не требовалось).
     * 2. [Set]<[String]> - список названий новых семестров (разница между новыми и старыми).
     */
    suspend fun updateSemesters(): Pair<Student?, Set<String>> {
        val cache = storage.loadStudent()
        if (cache == null || (cache.cacheTime subHours DateTime.now() < 1)) {
            return cache?.data to emptySet()
        }

        val newStudent = journal.student(useCache = false)
        return newStudent to newStudent.semesters.subtract(cache.data.semesters.toSet())
    }

    /**
     * Проверяет изменения оценок в конкретном семестре.
     *
     * Сравнивает кэшированные оценки с актуальными данными с сервера.
     * Если с момента последнего кэширования прошло меньше часа, обновление не выполняется.
     *
     * @param semester Название семестра для проверки.
     * @return Набор строк с описанием изменений (например, "Математика: 45 (М1)").
     */
    suspend fun updateSemesterMarks(semester: String): Set<String> {
        val cache = storage.loadSemester(semester)
        if (cache == null || (cache.cacheTime subHours DateTime.now() < 1)) {
            return emptySet()
        }

        val newSemesterMarks = journal.semesterMarks(semester, useCache = false)

        val changes = mutableSetOf<String>()
        for (newDiscipline in newSemesterMarks) {
            for (oldDiscipline in cache.data) {
                if (newDiscipline.title == oldDiscipline.title) {
                    for (type in MarkType.entries) {
                        val newMark = newDiscipline[type]
                        val oldMark = oldDiscipline[type]
                        if (newMark != null && oldMark != null && newMark != oldMark) {
                            changes += "${newDiscipline.title}: $newMark (${type.tag})"
                        }
                    }
                }
            }
        }

        return changes
    }
}