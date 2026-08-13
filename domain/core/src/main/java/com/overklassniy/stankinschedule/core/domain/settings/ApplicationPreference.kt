package com.overklassniy.stankinschedule.core.domain.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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
     * Включена ли отправка данных (Firebase Analytics и Sentry).
     *
     * @return `true`, если сбор данных включён. Значение по умолчанию: `true`.
     */
    suspend fun isAnalyticsEnabled(): Boolean {
        return repository.getBoolean(FIREBASE_ANALYTICS, true)
    }

    /**
     * Устанавливает флаг включения сбора данных (Firebase Analytics и Sentry).
     *
     * @param value `true` для включения сбора данных.
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
     * Устаревший метод. Используйте [currentThemePreset].
     *
     * @return Текущий [DarkMode] или [DarkMode.Default], если значение не установлено.
     */
    @Deprecated("Заменён на currentThemePreset(). Используйте ThemePreset.", ReplaceWith("currentThemePreset()"))
    @Suppress("DEPRECATION")
    suspend fun currentDarkMode(): DarkMode {
        return DarkMode.from(repository.getString(DARK_MODE)) ?: DarkMode.Default
    }

    /**
     * Устанавливает режим темы оформления.
     *
     * Устаревший метод. Используйте [setThemePreset].
     *
     * @param mode Новый режим [DarkMode].
     */
    @Deprecated("Заменён на setThemePreset(). Используйте ThemePreset.", ReplaceWith("setThemePreset(ThemePreset.System)"))
    @Suppress("DEPRECATION")
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

    /**
     * Возвращает текущий пресет темы оформления.
     *
     * При первом запуске после обновления выполняется миграция со старого формата [DarkMode]:
     * - [DarkMode.Default] -> [ThemePreset.System]
     * - [DarkMode.Dark] -> [ThemePreset.Dark]
     * - [DarkMode.Light] -> [ThemePreset.Light]
     *
     * @return Текущий [ThemePreset] или [ThemePreset.System], если значение не установлено.
     */
    @Suppress("DEPRECATION")
    suspend fun currentThemePreset(): ThemePreset {
        val stored = repository.getString(THEME_PRESET)
        if (stored != null) {
            return ThemePreset.from(stored) ?: ThemePreset.System
        }

        // Миграция со старого формата DarkMode
        val oldDarkMode = DarkMode.from(repository.getString(DARK_MODE))
        val mapped = when (oldDarkMode) {
            DarkMode.Dark -> ThemePreset.Dark
            DarkMode.Light -> ThemePreset.Light
            else -> ThemePreset.System
        }
        repository.saveString(THEME_PRESET, mapped.tag)
        return mapped
    }

    /**
     * Устанавливает пресет темы оформления.
     *
     * При переключении на [ThemePreset.Custom] сохраняет все цвета текущего
     * пресета как базовые, чтобы кастомная тема наследовала тёмную или светлую
     * схему в зависимости от того, что было выбрано до этого.
     *
     * @param preset Новый пресет [ThemePreset].
     */
    suspend fun setThemePreset(preset: ThemePreset) {
        if (preset == ThemePreset.Custom) {
            // Сохраняем цвета текущего пресета как базу для Custom
            val currentPreset = currentThemePreset()
            if (currentPreset != ThemePreset.Custom) {
                savePresetColors(currentPreset)
            }
        }
        repository.saveString(THEME_PRESET, preset.tag)
    }

    /**
     * Возвращает Flow с текущим пресетом темы оформления.
     *
     * Используется в AppTheme для определения, нужно ли следовать
     * системной тёмной теме (пресет System) или использовать
     * фиксированные цвета (пресеты Light, Dark, Custom).
     *
     * @return Flow с текущим [ThemePreset].
     */
    fun themePresetFlow(): Flow<ThemePreset> {
        return repository.getStringFlow(THEME_PRESET).map {
            ThemePreset.from(it) ?: ThemePreset.System
        }
    }

    /**
     * Возвращает Flow с цветовой схемой Material 3.
     *
     * Для пресетов System, Light, Dark возвращаются встроенные значения
     * из [ThemeColors.lightDefault] или [ThemeColors.darkDefault].
     * Для пресета Custom читаются 6 ключевых цветов, из которых
     * через [ThemeColors.derive] вычисляются остальные 22 цвета.
     *
     * @return Flow с текущей цветовой схемой [ThemeColors].
     */
    fun themeColors(): Flow<ThemeColors> {
        val allFlows = listOf(THEME_PRESET) + THEME_COLOR_KEYS + listOf(THEME_TEXT_COLOR)
        val flows = allFlows.map { repository.getStringFlow(it) }

        return combine(flows) { values ->
            val preset = ThemePreset.from(values[0]) ?: ThemePreset.System

            val baseColors = if (preset != ThemePreset.Custom) {
                if (preset == ThemePreset.Dark) {
                    ThemeColors.darkDefault()
                } else {
                    ThemeColors.lightDefault()
                }
            } else {
                // Для Custom: читаем 6 ключевых цветов с fallback на lightDefault
                val defaults = KeyColors.lightDefault()
                fun valueOr(index: Int, default: String): String =
                    values[index]?.takeIf { it.isNotEmpty() } ?: default

                val keyColors = KeyColors(
                    primary = valueOr(1, defaults.primary),
                    secondary = valueOr(2, defaults.secondary),
                    tertiary = valueOr(3, defaults.tertiary),
                    background = valueOr(4, defaults.background),
                    surface = valueOr(5, defaults.surface),
                    error = valueOr(6, defaults.error),
                )

                ThemeColors.derive(keyColors)
            }

            // Применяем переопределение цвета текста, если оно задано
            val textColorOverride = values[7]?.takeIf { it.isNotEmpty() }
            if (textColorOverride != null) {
                baseColors.copy(
                    onBackground = textColorOverride,
                    onSurface = textColorOverride,
                )
            } else {
                baseColors
            }
        }
    }

    /**
     * Устанавливает цвет текста (onSurface и onBackground).
     *
     * Не переключает пресет на Custom, так как настройка доступна для всех пресетов.
     *
     * @param hex HEX-значение цвета в формате `#RRGGBB` или `#AARRGGBB`.
     */
    suspend fun setThemeTextColor(hex: String) {
        repository.saveString(THEME_TEXT_COLOR, hex)
    }

    /**
     * Сбрасывает переопределение цвета текста к автоматическому вычислению.
     */
    suspend fun resetThemeTextColor() {
        repository.saveString(THEME_TEXT_COLOR, "")
    }

    /**
     * Устанавливает отдельный цвет темы Material 3.
     *
     * При вызове автоматически переключает пресет на [ThemePreset.Custom],
     * так как происходит ручное изменение цвета.
     *
     * @param colorName Имя цвета (соответствует ключу в [THEME_COLOR_KEYS]).
     * @param hex HEX-значение цвета в формате `#RRGGBB` или `#AARRGGBB`.
     */
    suspend fun setThemeColor(colorName: String, hex: String) {
        val key = "theme_color_$colorName"

        // При переходе с встроенного пресета на Custom сохраняем все цвета
        // текущего пресета, чтобы несохранённые цвета не сбрасывались на lightDefault
        val currentPreset = currentThemePreset()
        if (currentPreset != ThemePreset.Custom) {
            savePresetColors(currentPreset)
        }

        repository.saveString(key, hex)
        repository.saveString(THEME_PRESET, ThemePreset.Custom.tag)
    }

    /**
     * Переключает пресет темы без удаления сохранённых цветов.
     *
     * Устанавливает указанный пресет, но не очищает сохранённые цвета,
     * чтобы пользователь мог вернуться на Custom без потери настроек.
     *
     * @param preset Пресет для переключения: [ThemePreset.Light], [ThemePreset.Dark],
     * [ThemePreset.System] или [ThemePreset.Custom].
     */
    suspend fun resetThemeColors(preset: ThemePreset) {
        repository.saveString(THEME_PRESET, preset.tag)
    }

    /**
     * Сохраняет ключевые цвета встроенного пресета в DataStore.
     *
     * Используется при переходе на Custom, чтобы базовые цвета
     * соответствовали выбранному ранее пресету (Dark или Light).
     *
     * @param preset Пресет, цвета которого нужно сохранить.
     */
    private suspend fun savePresetColors(preset: ThemePreset) {
        val keyColors = if (preset == ThemePreset.Dark) {
            KeyColors.darkDefault()
        } else {
            KeyColors.lightDefault()
        }
        val colorValues = listOf(
            keyColors.primary,
            keyColors.secondary,
            keyColors.tertiary,
            keyColors.background,
            keyColors.surface,
            keyColors.error,
        )
        for (i in THEME_COLOR_KEYS.indices) {
            repository.saveString(THEME_COLOR_KEYS[i], colorValues[i])
        }
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

        private const val THEME_PRESET = "theme_preset"
        private const val THEME_TEXT_COLOR = "theme_color_text"

        // Ключи для 6 ключевых цветов Material 3 в формате "theme_color_<name>"
        private val THEME_COLOR_KEYS = listOf(
            "theme_color_primary",
            "theme_color_secondary",
            "theme_color_tertiary",
            "theme_color_background",
            "theme_color_surface",
            "theme_color_error",
        )
    }
}