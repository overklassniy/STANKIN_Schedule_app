package com.overklassniy.stankinschedule.core.domain.settings

import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Класс-обертка для управления настройками приложения.
 *
 * Предоставляет типизированный доступ к настройкам, хранящимся в [PreferenceRepository].
 * Управляет такими параметрами как аналитика, тема оформления, язык приложения и информация об обновлениях.
 *
 * Все методы являются suspend, так как хранилище (DataStore) работает асинхронно.
 *
 * @property repository Репозиторий для низкоуровневой работы с хранилищем настроек.
 */
class ApplicationPreference @Inject constructor(
    private val repository: PreferenceRepository
) {

    /**
     * Включена ли отправка аналитики (Firebase Analytics).
     *
     * @return `true`, если аналитика включена. Значение по умолчанию: `true`.
     */
    suspend fun isAnalyticsEnabled(): Boolean {
        return repository.getBoolean(FIREBASE_ANALYTICS, true)
    }

    /**
     * Устанавливает флаг включения аналитики.
     *
     * @param value `true` для включения аналитики.
     */
    suspend fun setAnalyticsEnabled(value: Boolean) {
        repository.saveBoolean(FIREBASE_ANALYTICS, value)
    }

    /**
     * Возвращает время последнего обновления внутри приложения (In-App Update).
     *
     * @return [DateTime] последнего обновления или null.
     */
    suspend fun lastInAppUpdate(): DateTime? {
        return repository.getDateTime(LAST_IN_APP_UPDATE)
    }

    /**
     * Сохраняет время последнего обновления внутри приложения.
     *
     * @param value Время обновления.
     */
    suspend fun setLastInAppUpdate(value: DateTime) {
        repository.saveDateTime(LAST_IN_APP_UPDATE, value)
    }

    /**
     * Возвращает время последней проверки наличия обновлений.
     *
     * @return [DateTime] последней проверки или null.
     */
    suspend fun lastUpdateCheck(): DateTime? {
        return repository.getDateTime(LAST_UPDATE_CHECK)
    }

    /**
     * Сохраняет время последней проверки наличия обновлений.
     *
     * @param value Время проверки.
     */
    suspend fun setLastUpdateCheck(value: DateTime) {
        repository.saveDateTime(LAST_UPDATE_CHECK, value)
    }

    /**
     * Возвращает версию доступного обновления приложения.
     *
     * @return Строку версии или null.
     */
    suspend fun availableUpdateVersion(): String? {
        return repository.getString(AVAILABLE_UPDATE_VERSION)
    }

    /**
     * Сохраняет версию доступного обновления.
     *
     * @param value Версия обновления.
     */
    suspend fun setAvailableUpdateVersion(value: String) {
        repository.saveString(AVAILABLE_UPDATE_VERSION, value)
    }

    /**
     * Возвращает список изменений (changelog) доступного обновления.
     *
     * @return Список изменений или null.
     */
    suspend fun availableUpdateChangelog(): String? {
        return repository.getString(AVAILABLE_UPDATE_CHANGELOG)
    }

    /**
     * Сохраняет список изменений доступного обновления.
     *
     * @param value Список изменений.
     */
    suspend fun setAvailableUpdateChangelog(value: String) {
        repository.saveString(AVAILABLE_UPDATE_CHANGELOG, value)
    }

    /**
     * Возвращает ссылку на скачивание доступного обновления.
     *
     * @return URL ссылки или null.
     */
    suspend fun availableUpdateUrl(): String? {
        return repository.getString(AVAILABLE_UPDATE_URL)
    }

    /**
     * Сохраняет ссылку на скачивание доступного обновления.
     *
     * @param value URL ссылки.
     */
    suspend fun setAvailableUpdateUrl(value: String) {
        repository.saveString(AVAILABLE_UPDATE_URL, value)
    }

    /**
     * Проверяет, есть ли доступное обновление.
     *
     * @return `true`, если версия обновления не пустая, иначе `false`.
     */
    suspend fun hasUpdate(): Boolean {
        val available = availableUpdateVersion()
        return !available.isNullOrEmpty()
    }

    /**
     * Очищает информацию о доступном обновлении.
     * Сбрасывает версию, список изменений и ссылку.
     */
    suspend fun clearUpdate() {
        setAvailableUpdateVersion("")
        setAvailableUpdateChangelog("")
        setAvailableUpdateUrl("")
    }

    /**
     * Возвращает результат последней проверки доступности приложения в Google Play.
     *
     * @return `true`/`false` или null, если проверка ещё не выполнялась.
     */
    suspend fun isGooglePlayAvailable(): Boolean? {
        return repository.getString(GOOGLE_PLAY_AVAILABLE)?.takeIf { it.isNotEmpty() }?.toBooleanStrictOrNull()
    }

    /**
     * Сохраняет результат проверки доступности приложения в Google Play.
     *
     * @param value Результат проверки или null для сброса.
     */
    suspend fun setGooglePlayAvailable(value: Boolean?) {
        repository.saveString(GOOGLE_PLAY_AVAILABLE, value?.toString() ?: "")
    }

    /**
     * Возвращает время последней проверки доступности приложения в Google Play.
     *
     * @return [DateTime] последней проверки или null.
     */
    suspend fun lastGooglePlayCheck(): DateTime? {
        return repository.getDateTime(LAST_GOOGLE_PLAY_CHECK)
    }

    /**
     * Сохраняет время последней проверки доступности приложения в Google Play.
     *
     * @param value Время проверки.
     */
    suspend fun setLastGooglePlayCheck(value: DateTime) {
        repository.saveDateTime(LAST_GOOGLE_PLAY_CHECK, value)
    }

    /**
     * Возвращает текущий режим темы оформления.
     *
     * @return Текущий [DarkMode] или [DarkMode.Default], если значение не установлено.
     */
    suspend fun currentDarkMode(): DarkMode {
        return DarkMode.from(repository.getString(DARK_MODE)) ?: DarkMode.Default
    }

    /**
     * Устанавливает режим темы оформления.
     *
     * @param mode Новый режим [DarkMode].
     */
    suspend fun setDarkMode(mode: DarkMode) {
        repository.saveString(DARK_MODE, mode.tag)
    }

    /**
     * Возвращает текущий язык приложения.
     *
     * @return Текущий [AppLanguage] или [AppLanguage.System], если значение не установлено.
     */
    suspend fun currentAppLanguage(): AppLanguage {
        return AppLanguage.from(repository.getString(APP_LANGUAGE)) ?: AppLanguage.System
    }

    /**
     * Устанавливает язык приложения.
     *
     * @param language Новый язык [AppLanguage].
     */
    suspend fun setAppLanguage(language: AppLanguage) {
        repository.saveString(APP_LANGUAGE, language.tag)
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
        private const val GOOGLE_PLAY_AVAILABLE = "google_play_available"
        private const val LAST_GOOGLE_PLAY_CHECK = "last_google_play_check"
    }
}