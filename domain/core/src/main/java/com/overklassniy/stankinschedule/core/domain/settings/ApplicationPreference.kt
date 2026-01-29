package com.overklassniy.stankinschedule.core.domain.settings

import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Класс-обертка для управления настройками приложения.
 *
 * Предоставляет типизированный доступ к настройкам, хранящимся в [PreferenceRepository].
 * Управляет такими параметрами как аналитика, тема оформления, язык приложения и информация об обновлениях.
 *
 * @property manager Репозиторий для низкоуровневой работы с хранилищем настроек.
 */
class ApplicationPreference @Inject constructor(
    private val manager: PreferenceRepository
) {

    /**
     * Включена ли отправка аналитики (Firebase Analytics).
     * Значение по умолчанию: `true`.
     */
    var isAnalyticsEnabled: Boolean
        get() = manager.getBoolean(FIREBASE_ANALYTICS, true)
        set(value) = manager.saveBoolean(FIREBASE_ANALYTICS, value)

    /**
     * Время последнего обновления внутри приложения (In-App Update).
     */
    var lastInAppUpdate: DateTime?
        get() = manager.getDateTime(LAST_IN_APP_UPDATE)
        set(value) {
            if (value != null) {
                manager.saveDateTime(LAST_IN_APP_UPDATE, value)
            }
        }

    /**
     * Время последней проверки наличия обновлений.
     */
    var lastUpdateCheck: DateTime?
        get() = manager.getDateTime(LAST_UPDATE_CHECK)
        set(value) {
            if (value != null) {
                manager.saveDateTime(LAST_UPDATE_CHECK, value)
            }
        }

    /**
     * Версия доступного обновления приложения.
     */
    var availableUpdateVersion: String?
        get() = manager.getString(AVAILABLE_UPDATE_VERSION)
        set(value) = manager.saveString(AVAILABLE_UPDATE_VERSION, value ?: "")

    /**
     * Список изменений (changelog) доступного обновления.
     */
    var availableUpdateChangelog: String?
        get() = manager.getString(AVAILABLE_UPDATE_CHANGELOG)
        set(value) = manager.saveString(AVAILABLE_UPDATE_CHANGELOG, value ?: "")

    /**
     * Ссылка на скачивание доступного обновления.
     */
    var availableUpdateUrl: String?
        get() = manager.getString(AVAILABLE_UPDATE_URL)
        set(value) = manager.saveString(AVAILABLE_UPDATE_URL, value ?: "")

    /**
     * Проверяет, есть ли доступное обновление.
     *
     * @return `true`, если версия обновления не пустая, иначе `false`.
     */
    fun hasUpdate(): Boolean {
        val available = availableUpdateVersion
        return !available.isNullOrEmpty()
    }

    /**
     * Очищает информацию о доступном обновлении.
     * Сбрасывает версию, список изменений и ссылку.
     */
    fun clearUpdate() {
        availableUpdateVersion = null
        availableUpdateChangelog = null
        availableUpdateUrl = null
    }

    /**
     * Возвращает текущий режим темы оформления.
     *
     * @return Текущий [DarkMode] или [DarkMode.Default], если значение не установлено.
     */
    fun currentDarkMode(): DarkMode {
        return DarkMode.from(manager.getString(DARK_MODE)) ?: DarkMode.Default
    }

    /**
     * Устанавливает режим темы оформления.
     *
     * @param mode Новый режим [DarkMode].
     */
    fun setDarkMode(mode: DarkMode) {
        manager.saveString(DARK_MODE, mode.tag)
    }

    /**
     * Возвращает текущий язык приложения.
     *
     * @return Текущий [AppLanguage] или [AppLanguage.System], если значение не установлено.
     */
    fun currentAppLanguage(): AppLanguage {
        return AppLanguage.from(manager.getString(APP_LANGUAGE)) ?: AppLanguage.System
    }

    /**
     * Устанавливает язык приложения.
     *
     * @param language Новый язык [AppLanguage].
     */
    fun setAppLanguage(language: AppLanguage) {
        manager.saveString(APP_LANGUAGE, language.tag)
    }

    companion object {
        private const val FIREBASE_ANALYTICS = "firebase_analytics"
        private const val DARK_MODE = "dark_mode_v2"
        private const val LAST_IN_APP_UPDATE = "last_in_app_update"
        private const val APP_LANGUAGE = "app_language"
        private const val LAST_UPDATE_CHECK = "last_update_check"
        private const val AVAILABLE_UPDATE_VERSION = "available_update_version"
        private const val AVAILABLE_UPDATE_CHANGELOG = "available_update_changelog"
        private const val AVAILABLE_UPDATE_URL = "available_update_url"
    }
}