package com.overklassniy.stankinschedule.schedule.editor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.DateEmptyException
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateItem
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.usecase.PairUseCase
import com.overklassniy.stankinschedule.schedule.editor.ui.components.DateRequest
import com.overklassniy.stankinschedule.schedule.editor.ui.components.DateResult
import com.overklassniy.stankinschedule.schedule.editor.ui.components.EditorMode
import com.overklassniy.stankinschedule.schedule.editor.ui.components.PairEditorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel редактора пары.
 *
 * Управляет загрузкой пары, состоянием дат, обработкой ошибок и применением изменений.
 * Обменивается событиями выбора даты через каналы pickerRequests/pickerResults.
 */
@HiltViewModel
class PairEditorViewModel @Inject constructor(
    private val useCase: PairUseCase,
) : ViewModel() {

    private val _pickerResults = Channel<DateResult>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    val pickerResults = _pickerResults.receiveAsFlow()

    private val _pickerRequests = Channel<DateRequest>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    val pickerRequests = _pickerRequests.receiveAsFlow()

    private val _scheduleErrors = Channel<Exception>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    val scheduleErrors = _scheduleErrors.receiveAsFlow()

    private val _pair = MutableStateFlow<PairEditorState>(PairEditorState.Loading)
    val pair = _pair.asStateFlow()

    private val _date = MutableStateFlow(DateModel())
    val date = _date.asStateFlow()

    private var scheduleId: Long = -1

    /**
     * Загружает пару для редактирования или инициализирует создание новой.
     *
     * @param scheduleId Идентификатор расписания.
     * @param pairId Идентификатор пары, null для создания новой.
     * @param mode Режим редактора: создание или редактирование.
     */
    fun loadPair(scheduleId: Long, pairId: Long?, mode: EditorMode) {
        this.scheduleId = scheduleId

        if (mode == EditorMode.Create || pairId == null) {
            _pair.value = PairEditorState.Content(null)
            return
        }

        viewModelScope.launch {
            val newPair = useCase.pair(pairId)

            _pair.value = PairEditorState.Content(newPair)
            if (newPair != null) {
                _date.value = newPair.date.clone()
            }
        }
    }

    /**
     * Публикует запрос на выбор даты.
     *
     * @param request Параметры запроса для диалога выбора даты.
     */
    fun onDateRequest(request: DateRequest) {
        viewModelScope.launch {
            _pickerRequests.send(request)
        }
    }

    /**
     * Передаёт результат выбора даты подписчикам.
     *
     * @param result Результат выбора даты с идентификатором запроса.
     */
    fun onDateResult(result: DateResult) {
        viewModelScope.launch {
            _pickerResults.send(result)
        }
    }

    /**
     * Заменяет существующую дату новой.
     *
     * @param old Старая дата.
     * @param new Новая дата.
     */
    fun editDate(old: DateItem, new: DateItem) {
        _date.value = _date.value.clone().apply {
            remove(old)
            add(new)
        }
    }

    /**
     * Добавляет новую дату в текущую модель.
     *
     * @param new Новая дата.
     */
    fun newDate(new: DateItem) {
        _date.value = _date.value.clone().apply {
            add(new)
        }
    }

    /**
     * Удаляет указанную дату из текущей модели.
     *
     * @param date Дата для удаления.
     */
    fun removeDate(date: DateItem) {
        _date.value = _date.value.clone().apply {
            remove(date)
        }
    }

    /**
     * Применяет изменения пары.
     *
     * Проверяет, что список дат не пуст, затем вызывает useCase.changePair.
     * Ошибки домена отправляет в канал scheduleErrors.
     *
     * @param newPair Новая модель пары.
     * @throws DateEmptyException Если даты не указаны.
     */
    fun applyPair(newPair: PairModel) {
        viewModelScope.launch {
            try {
                if (newPair.date.isEmpty()) {
                    throw DateEmptyException()
                }

                val pair = _pair.value.getOrNull()

                useCase.changePair(scheduleId, pair, newPair)
                _pair.value = PairEditorState.Complete

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _scheduleErrors.send(e)
            }
        }
    }

    /**
     * Удаляет текущую редактируемую пару.
     *
     * Если пара существует, вызывает useCase.deletePair, затем завершает состояние.
     */
    fun deletePair() {
        viewModelScope.launch {
            try {
                val pair = _pair.value.getOrNull()
                if (pair != null) {
                    useCase.deletePair(pair)
                }
                _pair.value = PairEditorState.Complete

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _scheduleErrors.send(e)
            }
        }
    }

    /**
     * Возвращает пару из состояния Content, иначе null.
     *
     * @receiver Состояние редактора пары.
     * @return Текущая редактируемая пара или null, если состояние не Content.
     */
    private fun PairEditorState.getOrNull(): PairModel? {
        return if (this is PairEditorState.Content) pair else null
    }
}