package com.overklassniy.stankinschedule.journal.viewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.core.ui.components.UIState
import com.overklassniy.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.core.domain.usecase.JournalUseCase
import com.overklassniy.stankinschedule.journal.core.domain.usecase.LoginUseCase
import com.overklassniy.stankinschedule.journal.core.domain.usecase.PredictUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана просмотра журнала.
 *
 * Управляет авторизацией, состоянием студента, текущим и предсказанным рейтингом,
 * постраничной загрузкой оценок по семестрам и настройками уведомлений.
 */
@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journal: JournalUseCase,
    private val login: LoginUseCase,
    private val predict: PredictUseCase,
    private val logger: LoggerAnalytics
) : ViewModel() {

    private val _isSignIn = MutableStateFlow(true)
    val isSignIn = _isSignIn.asStateFlow()

    val isNotification = journal.isUpdateMarksAllow()

    private val _student = MutableStateFlow<UIState<Student>>(UIState.loading())
    val student = _student.asStateFlow()

    private val _rating = MutableStateFlow<String?>(null)
    val rating = _rating.asStateFlow()

    private val _predictedRating = MutableStateFlow<String?>(null)
    val predictedRating = _predictedRating.asStateFlow()

    private val _isForceRefreshing = MutableStateFlow(false)
    val isForceRefreshing = _isForceRefreshing.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
            /**
             * Поток постраничных данных по оценкам семестров.
             *
             * Генерируется при успешной загрузке данных студента и
             * кэшируется в пределах [viewModelScope].
             */
    val semesters: Flow<PagingData<SemesterMarks>> = _student.flatMapLatest { state ->
        if (state is UIState.Success) createPager(state.data).flow else emptyFlow()
    }.flowOn(Dispatchers.IO).cachedIn(viewModelScope)


    init {
        updateStudentInfo()
    }

    /**
     * Инициализирует состояние экрана на основе данных студента.
     *
     * Устанавливает успешное состояние, сбрасывает флаг принудительного
     * обновления и запускает вычисление текущего и предсказанного рейтинга.
     *
     * @param student Загруженные данные студента.
     */
    private fun setupStudent(student: Student) {
        _student.value = UIState.success(student)
        _isForceRefreshing.value = false

        updateRating(student)
        updatePredictRating(student)
    }

    /**
     * Создаёт пейджер для постраничной загрузки оценок по семестрам.
     *
     * Конфигурация: размер страницы — 1, начальный ключ — первый семестр студента.
     * Источник данных предоставляет [JournalUseCase.semesterSource].
     *
     * @param data Данные студента.
     * @return Pager для получения [SemesterMarks].
     */
    private fun createPager(data: Student): Pager<String, SemesterMarks> {
        return Pager(
            config = PagingConfig(pageSize = 1),
            initialKey = data.semesters.first(),
            pagingSourceFactory = { journal.semesterSource(data) }
        )
    }

    /**
     * Загружает информацию о студенте и обновляет состояние экрана.
     *
     * При потере авторизации переводит флаг isSignIn в false; при прочих ошибках
     * устанавливает состояние ошибки. В случае успеха вызывает [setupStudent].
     *
     * @param useCache Если true — допускается использование кеша и показ Loading.
     */
    private fun updateStudentInfo(useCache: Boolean = true) {
        viewModelScope.launch {
            journal.student(useCache = useCache)
                .catch { t ->
                    if (t is StudentAuthorizedException) {
                        _isSignIn.value = false
                    } else {
                        _student.value = UIState.failed(t)
                    }
                }
                .collect { student ->
                    if (student == null) {
                        _isSignIn.value = false
                    } else {
                        setupStudent(student)
                    }
                }
        }
    }

    /**
     * Обновляет текущий рейтинг студента.
     *
     * Запрашивает рейтинг из [PredictUseCase.rating]. При ошибке сбрасывает значение
     * в null; при успехе устанавливает полученное значение в [_rating].
     *
     * @param student Студент, для которого вычисляется текущий рейтинг.
     */
    private fun updateRating(student: Student) {
        viewModelScope.launch {
            predict.rating(student)
                .catch {
                    _rating.value = null
                }
                .collect { currentRating ->
                    _rating.value = currentRating
                }
        }
    }

    /**
     * Обновляет предсказанный рейтинг студента.
     *
     * Запрашивает значение из [PredictUseCase.predictRating]. При ошибке сбрасывает
     * значение в null; при успехе устанавливает полученное значение в [_predictedRating].
     *
     * @param student Студент, для которого вычисляется предсказанный рейтинг.
     */
    private fun updatePredictRating(student: Student) {
        viewModelScope.launch {
            predict.predictRating(student)
                .catch {
                    _predictedRating.value = null
                }
                .collect { currentRating ->
                    _predictedRating.value = currentRating
                }
        }
    }

    /**
     * Обновляет информацию о студенте.
     *
     * @param useCache Если true — перед обновлением показывает состояние Loading.
     */
    fun refreshStudentInfo(useCache: Boolean) {
        if (_student.value !is UIState.Loading) {

            _isForceRefreshing.value = true
            if (useCache) {
                _student.value = UIState.loading()
            }

            updateStudentInfo(useCache = useCache)
        }
    }

    /**
     * Включает/выключает фоновые обновления оценок и уведомления.
     *
     * @param allow Разрешение на обновление оценок в фоне.
     */
    fun setUpdateMarksAllow(allow: Boolean) {
        viewModelScope.launch {
            journal.setUpdateMarksAllow(allow)
        }
    }

    /**
     * Выполняет выход из аккаунта и переводит состояние авторизации в false.
     * При ошибке записывает исключение в аналитику.
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                login.signOut()
            } catch (e: Exception) {
                logger.recordException(e)
            }
            _isSignIn.value = false
        }
    }
}