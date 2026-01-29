package com.overklassniy.stankinschedule.journal.core.domain.usecase

import androidx.paging.PagingSource
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalPagingRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalPreference
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Основной UseCase для работы с журналом оценок в UI.
 *
 * Предоставляет методы для получения данных студента, списка семестров и управления настройками.
 */
class JournalUseCase @Inject constructor(
    private val journal: JournalRepository,
    private val paging: JournalPagingRepository,
    private val preference: JournalPreference
) {
    /**
     * Получает поток данных о разрешении автоматического обновления оценок.
     *
     * @return [Flow] с состоянием разрешения.
     */
    fun isUpdateMarksAllow(): Flow<Boolean> = preference.isUpdateMarksAllow()

    /**
     * Устанавливает разрешение на автоматическое обновление оценок.
     *
     * @param allow `true` - разрешить, `false` - запретить.
     */
    suspend fun setUpdateMarksAllow(allow: Boolean) = preference.setUpdateMarksAllow(allow)

    /**
     * Создает источник данных для постраничного отображения семестров.
     *
     * @param student Объект студента, содержащий список семестров.
     * @return [PagingSource] для загрузки данных семестров.
     */
    fun semesterSource(student: Student): PagingSource<String, SemesterMarks> {
        return paging.semesterSource(
            journal = journal,
            semesters = student.semesters,
            semesterExpireHours = 2
        )
    }

    /**
     * Загружает данные студента.
     *
     * @param useCache Использовать ли кэш.
     * @return [Flow] с объектом [Student] или null.
     */
    fun student(useCache: Boolean): Flow<Student?> = flow {
        val student = journal.student(useCache)
        emit(student)
    }.flowOn(Dispatchers.IO)
}