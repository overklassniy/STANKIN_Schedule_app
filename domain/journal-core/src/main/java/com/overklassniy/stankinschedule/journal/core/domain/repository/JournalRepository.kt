package com.overklassniy.stankinschedule.journal.core.domain.repository

import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student


interface JournalRepository {

    suspend fun student(useCache: Boolean = true): Student

    suspend fun semesterMarks(
        semester: String,
        semesterExpireHours: Int = 2,
        useCache: Boolean = true,
    ): SemesterMarks
}