package com.overklassniy.stankinschedule.news.widget.ui.configure

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision
import com.overklassniy.stankinschedule.news.widget.domain.model.NewsWidgetData
import com.overklassniy.stankinschedule.news.widget.domain.usecase.NewsConfigureUseCase
import com.overklassniy.stankinschedule.news.widget.ui.glance.NewsWidgetStateKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel конфигурации виджета новостей.
 *
 * Управляет загрузкой списка категорий новостей и сохранённых настроек виджета
 * через Glance state.
 *
 * @property applicationContext Контекст приложения для доступа к Glance API.
 * @property configureUseCase UseCase для получения списка категорий новостей.
 */
@HiltViewModel
class NewsWidgetConfigureViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val configureUseCase: NewsConfigureUseCase,
) : ViewModel() {

    private val _subdivisions = MutableStateFlow(emptyList<NewsSubdivision>())

    /** Публичный список доступных категорий новостей для выбора. */
    val subdivisions: StateFlow<List<NewsSubdivision>> = _subdivisions.asStateFlow()

    private val _currentData = MutableStateFlow<NewsWidgetData?>(null)

    /** Публичные сохранённые данные текущего виджета. */
    val currentData: StateFlow<NewsWidgetData?> = _currentData.asStateFlow()

    init {
        viewModelScope.launch {
            _subdivisions.value = configureUseCase.subdivisions()
        }
    }

    /**
     * Загружает сохранённые настройки виджета из Glance state.
     * Повторные вызовы игнорируются, если данные уже загружены.
     *
     * @param appWidgetId Идентификатор виджета.
     */
    fun loadConfigure(appWidgetId: Int) {
        if (_currentData.value != null) return
        viewModelScope.launch {
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceId = manager.getGlanceIdBy(appWidgetId)
            val prefs = getAppWidgetState<Preferences>(
                applicationContext,
                PreferencesGlanceStateDefinition,
                glanceId,
            )
            _currentData.value = NewsWidgetStateKeys.toData(prefs)
        }
    }
}