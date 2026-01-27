package com.overklassniy.stankinschedule.journal.core.data.repository

import com.overklassniy.stankinschedule.core.data.cache.CacheManager
import com.overklassniy.stankinschedule.core.domain.cache.CacheContainer
import com.overklassniy.stankinschedule.journal.core.data.mapper.MarkTypeTypeConverter
import com.overklassniy.stankinschedule.journal.core.domain.model.MarkType
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalStorageRepository
import javax.inject.Inject

/**
 * Реализация репозитория для локального хранения данных журнала [JournalStorageRepository].
 * Использует [CacheManager] для кэширования данных в файловой системе.
 *
 * @param cache Менеджер кэша для выполнения операций чтения и записи.
 */
class JournalStorageRepositoryImpl @Inject constructor(
    private val cache: CacheManager,
) : JournalStorageRepository {

    init {
        // Настройка пути кэширования и регистрация конвертеров типов
        cache.addStartedPath("module_journal")
        cache.configurateParser {
            registerTypeAdapter(MarkType::class.java, MarkTypeTypeConverter())
        }
    }

    /**
     * Сохраняет данные студента в кэш.
     *
     * @param student Объект [Student] для сохранения.
     */
    override suspend fun saveStudent(student: Student) {
        cache.saveToCache(student, "student")
    }

    /**
     * Загружает данные студента из кэша.
     *
     * @return Контейнер [CacheContainer] с данными студента или null, если кэш отсутствует/пуст.
     */
    override suspend fun loadStudent(): CacheContainer<Student>? {
        return cache.loadFromCache(Student::class.java, "student")
    }

    /**
     * Сохраняет оценки за семестр в кэш.
     * Использует идентификатор семестра в качестве ключа.
     *
     * @param semester Идентификатор семестра (ключ кэша).
     * @param marks Объект [SemesterMarks] с оценками.
     */
    override suspend fun saveSemester(semester: String, marks: SemesterMarks) {
        cache.saveToCache(marks, semester)
    }

    /**
     * Загружает оценки за семестр из кэша.
     *
     * @param semester Идентификатор семестра (ключ кэша).
     * @return Контейнер [CacheContainer] с оценками или null, если данные не найдены.
     */
    override suspend fun loadSemester(semester: String): CacheContainer<SemesterMarks>? {
        return cache.loadFromCache(SemesterMarks::class.java, semester)
    }

    /**
     * Очищает все кэшированные данные журнала.
     * Удаляет все файлы в директории кэша модуля.
     */
    override suspend fun clear() {
        cache.clearAll()
    }
}