package com.overklassniy.stankinschedule.settings.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.core.domain.ext.subHours
import com.overklassniy.stankinschedule.core.domain.model.AppUpdate
import com.overklassniy.stankinschedule.core.domain.repository.GooglePlayAvailabilityRepository
import com.overklassniy.stankinschedule.core.domain.repository.UpdateRepository
import com.overklassniy.stankinschedule.core.domain.settings.AppLanguage
import com.overklassniy.stankinschedule.core.domain.settings.ApplicationPreference
import com.overklassniy.stankinschedule.core.domain.settings.ThemeColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemePreset
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorType
import com.overklassniy.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import com.overklassniy.stankinschedule.schedule.widget.ui.glance.ScheduleWidgetUpdater
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val applicationPreference: ApplicationPreference,
    private val schedulePreference: SchedulePreference,
    private val updateRepository: UpdateRepository,
    private val googlePlayAvailabilityRepository: GooglePlayAvailabilityRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _themePreset = MutableStateFlow<ThemePreset?>(value = null)
    val themePreset: StateFlow<ThemePreset?> = _themePreset.asStateFlow()

    /**
     * Устанавливает пресет темы оформления.
     *
     * @param preset Новый пресет темы.
     */
    fun setThemePreset(preset: ThemePreset) {
        viewModelScope.launch {
            applicationPreference.setThemePreset(preset)
            _themePreset.value = preset
        }
    }

    // Flow с цветами Material 3 из DataStore
    val themeColors: Flow<ThemeColors> = applicationPreference.themeColors()

    /**
     * Устанавливает отдельный цвет темы Material 3.
     *
     * При вызове автоматически переключает пресет на Custom.
     *
     * @param colorName Имя цвета (ключ в THEME_COLOR_KEYS без префикса).
     * @param hex HEX-значение цвета.
     */
    fun setThemeColor(colorName: String, hex: String) {
        viewModelScope.launch {
            applicationPreference.setThemeColor(colorName, hex)
            _themePreset.value = ThemePreset.Custom
        }
    }

    /**
     * Сбрасывает все цвета Material 3 к встроенному пресету.
     *
     * @param preset Пресет для сброса.
     */
    fun resetThemeColors(preset: ThemePreset) {
        viewModelScope.launch {
            applicationPreference.resetThemeColors(preset)
            _themePreset.value = preset
        }
    }

    /**
     * Устанавливает цвет текста (onSurface и onBackground).
     *
     * Не переключает пресет на Custom, так как настройка доступна для всех пресетов.
     *
     * @param hex HEX-значение цвета.
     */
    fun setThemeTextColor(hex: String) {
        viewModelScope.launch {
            applicationPreference.setThemeTextColor(hex)
        }
    }

    /**
     * Сбрасывает переопределение цвета текста к автоматическому вычислению.
     */
    fun resetThemeTextColor() {
        viewModelScope.launch {
            applicationPreference.resetThemeTextColor()
        }
    }

    private val _appLanguage = MutableStateFlow<AppLanguage?>(value = null)
    val appLanguage: StateFlow<AppLanguage?> = _appLanguage.asStateFlow()

    fun setAppLanguage(language: AppLanguage) {
        viewModelScope.launch {
            applicationPreference.setAppLanguage(language)
            _appLanguage.value = language
        }
    }

    val isVerticalViewer: Flow<Boolean> = schedulePreference.isVerticalViewer()
    val pairColorGroup: Flow<PairColorGroup> = schedulePreference.scheduleColorGroup()

    fun setVerticalViewer(isVertical: Boolean) {
        viewModelScope.launch { schedulePreference.setVerticalViewer(isVertical) }
    }

    fun setPairColor(hex: String, type: PairColorType) {
        viewModelScope.launch {
            schedulePreference.setScheduleColor(hex, type)
            ScheduleWidgetUpdater.updateAll(context)
        }
    }

    private val _isAnalyticsEnabled = MutableStateFlow(true)
    val isAnalyticsEnabled = _isAnalyticsEnabled.asStateFlow()

    fun setAnalyticsEnabled(enable: Boolean) {
        viewModelScope.launch {
            applicationPreference.setAnalyticsEnabled(enable)
            _isAnalyticsEnabled.value = enable
        }
    }

    private val _availableUpdate = MutableStateFlow<AppUpdate?>(null)
    val availableUpdate: StateFlow<AppUpdate?> = _availableUpdate.asStateFlow()

    private val _googlePlayAvailable = MutableStateFlow(false)
    val googlePlayAvailable: StateFlow<Boolean> = _googlePlayAvailable.asStateFlow()

    init {
        loadCachedState()
        checkForUpdates()
    }

    /**
     * Загружает кэшированные значения настроек из DataStore.
     */
    private fun loadCachedState() {
        viewModelScope.launch {
            _themePreset.value = applicationPreference.currentThemePreset()
            _appLanguage.value = applicationPreference.currentAppLanguage()
            _isAnalyticsEnabled.value = applicationPreference.isAnalyticsEnabled()
            _googlePlayAvailable.value = applicationPreference.isGooglePlayAvailable() == true

            val version = applicationPreference.availableUpdateVersion()
            val changelog = applicationPreference.availableUpdateChangelog()
            val url = applicationPreference.availableUpdateUrl()

            if (!version.isNullOrEmpty()) {
                _availableUpdate.value = AppUpdate(
                    latestVersion = version,
                    changelog = changelog ?: "",
                    downloadUrl = url ?: "",
                    releaseName = "v$version"
                )
            }
        }
    }

    fun checkForUpdates(force: Boolean = false) {
        viewModelScope.launch {
            val lastCheck = applicationPreference.lastUpdateCheck()
            val shouldCheck = force || lastCheck == null || (lastCheck subHours DateTime.now()) > 24

            if (shouldCheck) {
                val currentVersion = appVersion()
                val update = updateRepository.checkForUpdate(currentVersion)

                applicationPreference.setLastUpdateCheck(DateTime.now())

                if (update != null) {
                    applicationPreference.setAvailableUpdateVersion(update.latestVersion)
                    applicationPreference.setAvailableUpdateChangelog(update.changelog)
                    applicationPreference.setAvailableUpdateUrl(update.downloadUrl)
                    _availableUpdate.value = update
                } else {
                    applicationPreference.clearUpdate()
                    _availableUpdate.value = null
                }

                checkGooglePlayAvailability()
            }
        }
    }

    private fun checkGooglePlayAvailability() {
        viewModelScope.launch {
            val lastCheck = applicationPreference.lastGooglePlayCheck()
            val shouldCheck = lastCheck == null || (lastCheck subHours DateTime.now()) > 24

            if (shouldCheck) {
                val available = googlePlayAvailabilityRepository.isAppAvailableOnPlayStore()
                applicationPreference.setGooglePlayAvailable(available)
                applicationPreference.setLastGooglePlayCheck(DateTime.now())
                _googlePlayAvailable.value = available
            }
        }
    }

    @Suppress("unused")
    fun dismissUpdate() {
        viewModelScope.launch {
            applicationPreference.clearUpdate()
            _availableUpdate.value = null
        }
    }

    private fun appVersion(): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "0.0.0"
        } catch (_: Exception) {
            "0.0.0"
        }
    }
}