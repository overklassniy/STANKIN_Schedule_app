package com.overklassniy.stankinschedule.schedule.widget.ui.configure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleItem
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.overklassniy.stankinschedule.schedule.widget.domain.usecase.ScheduleConfigureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel конфигурации виджета расписания.
 *
 * Управляет загрузкой списков расписаний и сохранением настроек виджета.
 */
@HiltViewModel
class ScheduleWidgetConfigureViewModel @Inject constructor(
    private val useCase: ScheduleConfigureUseCase
) : ViewModel() {

    private val _schedules = MutableStateFlow(emptyList<ScheduleItem>())

    /** Публичный список доступных расписаний для выбора. */
    val schedules: StateFlow<List<ScheduleItem>> = _schedules.asStateFlow()

    private val _currentData = MutableStateFlow<ScheduleWidgetData?>(null)

    /** Публичные сохраненные данные текущего виджета. */
    val currentData = _currentData.asStateFlow()


    init {
        // Загружаем список расписаний при создании ViewModel.
        viewModelScope.launch {
            useCase.schedules().collect { list ->
                _schedules.value = list
            }
        }
    }

    /**
     * Загружает сохраненные настройки виджета.
     * Повторные вызовы игнорируются, если данные уже загружены.
     *
     * @param appWidgetId Идентификатор виджета.
     */
    fun loadConfigure(appWidgetId: Int) {
        if (_currentData.value != null) return
        _currentData.value = useCase.loadWidgetData(appWidgetId)
    }

    /**
     * Сохраняет настройки виджета.
     *
     * @param appWidgetId Идентификатор виджета.
     * @param data Данные для сохранения.
     */
    fun saveConfigure(appWidgetId: Int, data: ScheduleWidgetData) {
        useCase.saveWidgetData(appWidgetId, data)
    }
}
