package com.overklassniy.stankinschedule.journal.core.data.repository

import android.util.Log
import com.overklassniy.stankinschedule.core.data.cache.CacheManager
import com.overklassniy.stankinschedule.core.domain.cache.CacheContainer
import com.overklassniy.stankinschedule.journal.core.data.mapper.MarkTypeTypeConverter
import com.overklassniy.stankinschedule.journal.core.domain.model.JournalSemester
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
     * Семестры нормализуются по убыванию (от новых к старым), чтобы кэши,
     * записанные со старым порядком, отображались единообразно.
     *
     * Кэш, записанный старыми версиями приложения, несовместим с текущей моделью:
     * семестры были строками, а имена полей в JSON обфусцированы R8. Gson не бросает
     * исключение для отсутствующих полей и создает объект с null-полями, поэтому
     * такой кэш считается отсутствующим — данные будут загружены из сети заново.
     *
     * @return Контейнер [CacheContainer] с данными студента или null, если кэш отсутствует/пуст/устарел.
     */
    override suspend fun loadStudent(): CacheContainer<Student>? {
        val container = cache.loadFromCache(Student::class.java, "student") ?: return null
        return try {
            val student = container.data
            CacheContainer(
                data = Student(
                    name = student.name,
                    group = student.group,
                    semesters = student.semesters.sortedDescending()
                ),
                cacheTime = container.cacheTime
            )
        } catch (e: Exception) {
            Log.d(TAG, "loadStudent: ignored incompatible cache", e)
            null
        }
    }

    /**
     * Сохраняет оценки за семестр в кэш.
     * Использует ключ семестра [JournalSemester.cacheKey].
     *
     * @param semester Семестр (ключ кэша).
     * @param marks Объект [SemesterMarks] с оценками.
     */
    override suspend fun saveSemester(semester: JournalSemester, marks: SemesterMarks) {
        cache.saveToCache(marks, semester.cacheKey())
    }

    /**
     * Загружает оценки за семестр из кэша.
     *
     * @param semester Семестр (ключ кэша).
     * @return Контейнер [CacheContainer] с оценками или null, если данные не найдены.
     */
    override suspend fun loadSemester(semester: JournalSemester): CacheContainer<SemesterMarks>? {
        return cache.loadFromCache(SemesterMarks::class.java, semester.cacheKey())
    }

    /**
     * Очищает все кэшированные данные журнала.
     * Удаляет все файлы в директории кэша модуля.
     */
    override suspend fun clear() {
        cache.clearAll()
    }

    private companion object {
        const val TAG = "JournalStorageRepositoryImpl"
    }
}
