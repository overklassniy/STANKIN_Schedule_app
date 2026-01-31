package com.overklassniy.stankinschedule.schedule.creator.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.schedule.core.domain.usecase.ScheduleDeviceUseCase
import com.overklassniy.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.overklassniy.stankinschedule.schedule.creator.ui.components.CreateEvent
import com.overklassniy.stankinschedule.schedule.creator.ui.components.CreateState
import com.overklassniy.stankinschedule.schedule.creator.ui.components.ImportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ScheduleCreatorVM"

/**
 * ViewModel создания расписания.
 *
 * Управляет состояниями создания пустого расписания и импорта из файла.
 * Все операции выполняются в viewModelScope.
 */
@HiltViewModel
class ScheduleCreatorViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase,
    private val scheduleDeviceUseCase: ScheduleDeviceUseCase
) : ViewModel() {

    /**
     * Состояние диалога создания расписания.
     * null означает отсутствие активного диалога.
     */
    private val _createState = MutableStateFlow<CreateState?>(null)
    val createState = _createState.asStateFlow()

    /**
     * Состояние импорта расписания с устройства.
     * null означает отсутствие активного процесса импорта.
     */
    private val _importState = MutableStateFlow<ImportState?>(null)
    val importState = _importState.asStateFlow()

    /**
     * Сбрасывает состояние импорта.
     * Вызывается после обработки результата импорта в UI.
     */
    fun clearImportState() {
        _importState.value = null
    }

    /**
     * Обрабатывает событие запуска или отмены создания расписания.
     *
     * @param event Событие создания [CreateEvent].
     */
    fun onCreateSchedule(event: CreateEvent) {
        // Переключаем состояние диалога в зависимости от события
        _createState.value = when (event) {
            CreateEvent.Cancel -> null
            CreateEvent.New -> CreateState.New
        }
    }

    /**
     * Создает пустое расписание.
     *
     * Алгоритм:
     * 1. Вызывает use case создания.
     * 2. При ошибке публикует CreateState.Error.
     * 3. При успехе публикует CreateState.Success, иначе CreateState.AlreadyExist.
     *
     * @param scheduleName Имя нового расписания.
     */
    fun createSchedule(scheduleName: String) {
        viewModelScope.launch {
            scheduleUseCase.createEmptySchedule(scheduleName)
                // Обрабатываем ошибку потока создания и транслируем её в состояние
                .catch { e ->
                    _createState.value = CreateState.Error(e)
                }
                .collect { isCreated ->
                    // true означает успешное создание, false означает конфликт имени
                    _createState.value = if (isCreated) {
                        CreateState.Success
                    } else {
                        CreateState.AlreadyExist()
                    }
                }
        }
    }

    /**
     * Импортирует расписание из выбранного файла.
     *
     * @param uri Ссылка на документ.
     */
    fun importSchedule(uri: Uri) {
        Log.d(TAG, "importSchedule: uri=$uri")
        viewModelScope.launch {
            scheduleDeviceUseCase.loadFromDevice(uri.toString())
                // Ошибки чтения файла транслируем в состояние Failed
                .catch { e ->
                    Log.e(TAG, "importSchedule: error", e)
                    _importState.value = ImportState.Failed(e)
                }
                .collect { scheduleName ->
                    Log.d(TAG, "importSchedule: success, scheduleName=$scheduleName")
                    // При успехе передаем загруженную модель в состояние Success
                    _importState.value = ImportState.Success(scheduleName)
                }
        }
    }
}