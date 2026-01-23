package com.overklassniy.stankinschedule.journal.core.domain.repository

import androidx.paging.PagingSource
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks

interface JournalPagingRepository {
    fun semesterSource(
        journal: JournalRepository,
        semesters: List<String>,
        semesterExpireHours: Int
    ): PagingSource<String, SemesterMarks>
}