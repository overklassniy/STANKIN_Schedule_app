package com.overklassniy.stankinschedule.schedule.widget.ui.configure

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleItem
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.overklassniy.stankinschedule.schedule.widget.domain.usecase.ScheduleConfigureUseCase
import com.overklassniy.stankinschedule.schedule.widget.ui.glance.WidgetStateKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel конфигурации виджета расписания.
 *
 * Управляет загрузкой списков расписаний и сохранением настроек виджета
 * через Glance state.
 *
 * @property applicationContext Контекст приложения для доступа к Glance API.
 */
@HiltViewModel
class ScheduleWidgetConfigureViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
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
     * Загружает сохраненные настройки виджета из Glance state.
     * Повторные вызовы игнорируются, если данные уже загружены.
     *
     * @param appWidgetId Идентификатор виджета.
     */
    fun loadConfigure(appWidgetId: Int) {
        if (_currentData.value != null) return
        viewModelScope.launch {
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceId = manager.getGlanceIdBy(appWidgetId)
            val prefs = getAppWidgetState<androidx.datastore.preferences.core.Preferences>(
                applicationContext,
                PreferencesGlanceStateDefinition,
                glanceId
            )
            _currentData.value = WidgetStateKeys.toData(prefs)
        }
    }
}
