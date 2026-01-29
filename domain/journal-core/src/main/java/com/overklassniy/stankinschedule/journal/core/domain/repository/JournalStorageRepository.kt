package com.overklassniy.stankinschedule.journal.core.domain.repository

import com.overklassniy.stankinschedule.core.domain.cache.CacheContainer
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student

/**
 * Репозиторий для локального хранения данных журнала (кэширование).
 *
 * Позволяет сохранять и загружать данные о студенте и оценках без доступа к сети.
 */
interface JournalStorageRepository {

    /**
     * Загружает кэшированные данные о студенте.
     *
     * @return Контейнер [CacheContainer] с данными студента или `null`, если кэш отсутствует.
     */
    suspend fun loadStudent(): CacheContainer<Student>?

    /**
     * Сохраняет данные о студенте в кэш.
     *
     * @param student Объект [Student] для сохранения.
     */
    suspend fun saveStudent(student: Student)

    /**
     * Загружает кэшированные оценки за семестр.
     *
     * @param semester Название семестра.
     * @return Контейнер [CacheContainer] с оценками или `null`, если кэш отсутствует.
     */
    suspend fun loadSemester(semester: String): CacheContainer<SemesterMarks>?

    /**
     * Сохраняет оценки за семестр в кэш.
     *
     * @param semester Название семестра.
     * @param marks Объект [SemesterMarks] для сохранения.
     */
    suspend fun saveSemester(semester: String, marks: SemesterMarks)

    /**
     * Очищает всё локальное хранилище журнала (удаляет все кэшированные данные).
     */
    suspend fun clear()
}