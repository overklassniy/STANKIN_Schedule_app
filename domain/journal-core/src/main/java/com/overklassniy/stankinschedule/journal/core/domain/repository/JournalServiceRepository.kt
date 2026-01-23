package com.overklassniy.stankinschedule.journal.core.domain.repository

import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.model.StudentCredentials


interface JournalServiceRepository {

    suspend fun loadSemesters(credentials: StudentCredentials): Student

    suspend fun loadMarks(credentials: StudentCredentials, semester: String): SemesterMarks
}