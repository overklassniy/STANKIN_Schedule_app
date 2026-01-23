package com.overklassniy.stankinschedule.journal.core.data.repository

import com.overklassniy.stankinschedule.core.data.cache.CacheManager
import com.overklassniy.stankinschedule.core.domain.cache.CacheContainer
import com.overklassniy.stankinschedule.journal.core.data.mapper.MarkTypeTypeConverter
import com.overklassniy.stankinschedule.journal.core.domain.model.MarkType
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalStorageRepository
import javax.inject.Inject

class JournalStorageRepositoryImpl @Inject constructor(
    private val cache: CacheManager,
) : JournalStorageRepository {

    init {
        cache.addStartedPath("module_journal")
        cache.configurateParser {
            registerTypeAdapter(MarkType::class.java, MarkTypeTypeConverter())
        }
    }

    override suspend fun saveStudent(student: Student) {
        cache.saveToCache(student, "student")
    }

    override suspend fun loadStudent(): CacheContainer<Student>? {
        return cache.loadFromCache(Student::class.java, "student")
    }

    override suspend fun saveSemester(semester: String, marks: SemesterMarks) {
        cache.saveToCache(marks, semester)
    }

    override suspend fun loadSemester(semester: String): CacheContainer<SemesterMarks>? {
        return cache.loadFromCache(SemesterMarks::class.java, semester)
    }

    override suspend fun clear() {
        cache.clearAll()
    }
}