package com.overklassniy.stankinschedule.schedule.list.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.overklassniy.stankinschedule.schedule.settings.domain.usecase.ScheduleSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel экрана списка расписаний.
 *
 * Управляет состоянием списка, режимом редактирования, избранным, выбором элементов
 * и операциями перемещения/удаления.
 */
@HiltViewModel
class ScheduleScreenViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase,
    private val settingsUseCase: ScheduleSettingsUseCase
) : ViewModel() {

    private val _editableMode = MutableStateFlow(false)
    val editableMode = _editableMode.asStateFlow()

    private val _schedules = MutableStateFlow<List<ScheduleInfo>?>(null)
    val schedules: StateFlow<List<ScheduleInfo>?> = _schedules.asStateFlow()

    private val _favorite = MutableStateFlow<Long>(-1)
    val favorite = _favorite.asStateFlow()

    @Stable
    val selected = mutableStateMapOf<Int, Boolean>()

    init {
        viewModelScope.launch {
            scheduleUseCase.schedules()
                .collect { newSchedules ->
                    if (!_editableMode.value) {
                        _schedules.value = newSchedules
                    }
                }
        }
        viewModelScope.launch {
            settingsUseCase.favorite()
                .collect { newFavorite ->
                    _favorite.value = newFavorite
                }
        }
    }

    /**
     * Проверяет, изменился ли порядок расписаний относительно их позиции.
     *
     * @param list Текущий список расписаний.
     * @return true если обнаружено несовпадение позиции, иначе false.
     */
    private fun isSchedulesMoved(list: List<ScheduleInfo>): Boolean {
        for ((index, schedule) in list.withIndex()) {
            if (schedule.position != index) {
                return true
            }
        }
        return false
    }

    /**
     * Сохраняет позиции расписаний при выходе из режима редактирования.
     * Позиции обновляются только если они были изменены.
     */
    private fun saveSchedulePositions() {
        val data = _schedules.value ?: return

        viewModelScope.launch {
            if (isSchedulesMoved(data)) {
                scheduleUseCase.updatePositions(data)
            }
        }
    }

    /**
     * Перемещает элемент в списке с позиции from на позицию to.
     *
     * @param from Исходная позиция.
     * @param to Новая позиция.
     */
    fun schedulesMove(from: Int, to: Int) {
        _schedules.value = _schedules.value?.let {
            it.toMutableList().apply { add(to, removeAt(from)) }
        }

        /*
        val t = selected[to]
        selected[to] = selected[from] ?: false
        selected[from] = t ?: false
         */
    }

    /**
     * Включает или выключает режим редактирования.
     * Очищает выбор. При выключении сохраняет позиции.
     *
     * @param enable Признак включения режима.
     */
    fun setEditable(enable: Boolean) {
        _editableMode.value = enable
        selected.clear()

        if (!enable) {
            saveSchedulePositions()
        }
    }

    /**
     * Устанавливает выбранное расписание как «избранное».
     *
     * @param id Идентификатор расписания.
     */
    fun setFavorite(id: Long) {
        viewModelScope.launch {
            settingsUseCase.setFavorite(id)
        }
    }

    /**
     * Проверяет, выбран ли элемент.
     *
     * @param id Идентификатор расписания.
     * @return true если выбран, иначе false.
     */
    fun isSelected(id: Long): Boolean {
        return selected.getOrElse(id.toInt()) { false }
    }

    /**
     * Инвертирует выбор расписания.
     *
     * @param id Идентификатор расписания.
     */
    fun selectSchedule(id: Long) {
        val index = id.toInt()
        selected[index] = !selected.getOrElse(index) { false }
    }

    /**
     * Удаляет все выбранные расписания.
     * После удаления отключает режим редактирования и очищает выбор.
     */
    fun removeSelectedSchedules() {
        val data = _schedules.value ?: return
        val removed = data.filter { selected.containsKey(it.id.toInt()) }

        _editableMode.value = false
        selected.clear()

        viewModelScope.launch {
            scheduleUseCase.removeSchedules(removed)
        }
    }

    /**
     * Удаляет указанное расписание.
     *
     * @param schedule Модель расписания для удаления.
     */
    fun removeSchedule(schedule: ScheduleInfo) {
        viewModelScope.launch {
            scheduleUseCase.removeSchedules(listOf(schedule))
        }
    }
}
