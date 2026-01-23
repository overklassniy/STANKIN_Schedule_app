package com.overklassniy.stankinschedule.journal.core.data.repository

import com.overklassniy.stankinschedule.journal.core.data.api.ModuleJournalAPI
import com.overklassniy.stankinschedule.journal.core.data.mapper.toSemesterMarks
import com.overklassniy.stankinschedule.journal.core.data.mapper.toStudent
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.model.StudentCredentials
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import retrofit2.await
import javax.inject.Inject

class JournalServiceRepositoryImpl @Inject constructor(
    private val api: ModuleJournalAPI,
) : JournalServiceRepository {

    override suspend fun loadSemesters(
        credentials: StudentCredentials,
    ): Student {
        return api.getSemesters(credentials.login, credentials.password)
            .await()
            .toStudent()
    }

    override suspend fun loadMarks(
        credentials: StudentCredentials,
        semester: String,
    ): SemesterMarks {
        return api.getMarks(credentials.login, credentials.password, semester)
            .await()
            .toSemesterMarks()
    }
}