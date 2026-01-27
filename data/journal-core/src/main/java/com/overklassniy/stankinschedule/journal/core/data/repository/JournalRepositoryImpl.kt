package com.overklassniy.stankinschedule.journal.core.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import com.overklassniy.stankinschedule.core.domain.ext.subHours
import com.overklassniy.stankinschedule.core.domain.ext.subMinutes
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalSecureRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalStorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Реализация репозитория журнала [JournalRepository].
 * Отвечает за получение данных о студенте и оценках, управляя кэшированием и сетевыми запросами.
 *
 * @param context Контекст приложения, используется для проверки режима отладки.
 * @param service Репозиторий для выполнения сетевых запросов к API журнала.
 * @param secure Репозиторий для работы с защищенными учетными данными.
 * @param storage Репозиторий для локального хранения данных (кэш).
 */
class JournalRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val service: JournalServiceRepository,
    private val secure: JournalSecureRepository,
    private val storage: JournalStorageRepository,
) : JournalRepository {

    private val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    private val semesterLocker = Mutex()

    /**
     * Получает информацию о студенте (включая список семестров).
     *
     * Алгоритм:
     * 1. Проверяет наличие кэшированных данных, если [useCache] = true.
     * 2. Если кэш есть и он свежий (менее 24 часов), возвращает данные из кэша.
     * 3. Если кэша нет или он устарел:
     *    a. Получает подписанные учетные данные.
     *    b. Загружает данные с сервера через [service].
     *    c. Сохраняет полученные данные в кэш [storage].
     *
     * @param useCache Флаг использования кэша. Если true, пытается вернуть данные из кэша.
     * @return Объект [Student] с информацией о студенте и списком семестров.
     */
    override suspend fun student(useCache: Boolean): Student {

        val cache = storage.loadStudent()
        // Проверка валидности кэша (существует, разрешено использование, не устарел - 24 часа)
        if (cache != null && useCache && (cache.cacheTime subHours DateTime.now() < 24)) {
            if (isDebug) {
                val minutes = cache.cacheTime subMinutes DateTime.now()
                Log.d(TAG, "Student cache time: $minutes minute(-s)")
            }

            return cache.data
        }

        // Загрузка данных с сервера при отсутствии валидного кэша
        val studentCredentials = secure.signCredentials()
        val student = service.loadSemesters(studentCredentials)
        storage.saveStudent(student)

        return student
    }

    /**
     * Получает оценки за указанный семестр.
     * Использует мьютекс для синхронизации запросов.
     *
     * Алгоритм:
     * 1. Блокирует выполнение для потокобезопасности.
     * 2. Проверяет кэш, если [useCache] = true.
     * 3. Если кэш свежий (менее [semesterExpireHours]), возвращает его.
     * 4. Иначе загружает данные с сервера и обновляет кэш.
     *
     * @param semester Идентификатор семестра (строка).
     * @param semesterExpireHours Время жизни кэша в часах.
     * @param useCache Флаг использования кэша.
     * @return Объект [SemesterMarks] с оценками за семестр.
     */
    override suspend fun semesterMarks(
        semester: String,
        semesterExpireHours: Int,
        useCache: Boolean,
    ): SemesterMarks = semesterLocker.withLock {

        val cache = storage.loadSemester(semester)
        // Проверка валидности кэша для конкретного семестра
        if (cache != null && useCache && (cache.cacheTime subHours DateTime.now() < semesterExpireHours)) {
            if (isDebug) {
                val hours = cache.cacheTime subHours DateTime.now()
                Log.d(TAG, "Semester '$semester' cache time: $hours hour(-s)")
            }

            return cache.data
        }

        // Загрузка оценок с сервера
        val studentCredentials = secure.signCredentials()
        val marks = service.loadMarks(studentCredentials, semester)
        storage.saveSemester(semester, marks)

        return marks
    }

    companion object {
        private const val TAG = "JournalRepositoryImpl"
    }
}