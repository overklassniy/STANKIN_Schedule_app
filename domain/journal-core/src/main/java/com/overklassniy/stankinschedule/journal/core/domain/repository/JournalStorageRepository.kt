package com.overklassniy.stankinschedule.journal.core.domain.repository

import com.overklassniy.stankinschedule.core.domain.cache.CacheContainer
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student

interface JournalStorageRepository {

    suspend fun loadStudent(): CacheContainer<Student>?

    suspend fun saveStudent(student: Student)

    suspend fun loadSemester(semester: String): CacheContainer<SemesterMarks>?

    suspend fun saveSemester(semester: String, marks: SemesterMarks)

    suspend fun clear()
}