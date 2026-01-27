package com.overklassniy.stankinschedule.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.core.domain.settings.AppLanguage
import com.overklassniy.stankinschedule.core.domain.settings.ApplicationPreference
import com.overklassniy.stankinschedule.core.domain.settings.DarkMode
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorType
import com.overklassniy.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val applicationPreference: ApplicationPreference,
    private val schedulePreference: SchedulePreference
) : ViewModel() {

    /**
     * General settings
     */

    private val _nightMode = MutableStateFlow(value = applicationPreference.currentDarkMode())
    val nightMode: StateFlow<DarkMode> = _nightMode.asStateFlow()

    fun setNightMode(mode: DarkMode) {
        applicationPreference.setDarkMode(mode)
        _nightMode.value = mode
    }

    private val _appLanguage = MutableStateFlow(value = applicationPreference.currentAppLanguage())
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    fun setAppLanguage(language: AppLanguage) {
        applicationPreference.setAppLanguage(language)
        _appLanguage.value = language
    }

    /**
     * Schedule settings
     */

    val isVerticalViewer: Flow<Boolean> = schedulePreference.isVerticalViewer()
    val pairColorGroup: Flow<PairColorGroup> = schedulePreference.scheduleColorGroup()

    private val _colorChanged = MutableSharedFlow<PairColorType>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val colorChanged = _colorChanged.asSharedFlow()

    fun setVerticalViewer(isVertical: Boolean) {
        viewModelScope.launch { schedulePreference.setVerticalViewer(isVertical) }
    }

    fun setPairColor(hex: String, type: PairColorType) {
        viewModelScope.launch {
            schedulePreference.setScheduleColor(hex, type)
            _colorChanged.emit(type)
        }
    }

    private val _isAnalyticsEnabled = MutableStateFlow(applicationPreference.isAnalyticsEnabled)
    val isAnalyticsEnabled = _isAnalyticsEnabled.asStateFlow()

    fun setAnalyticsEnabled(enable: Boolean) {
        applicationPreference.isAnalyticsEnabled = enable
        _isAnalyticsEnabled.value = enable
    }
}