package com.overklassniy.stankinschedule.migration

import android.content.Context
import android.graphics.Color
import androidx.preference.PreferenceManager
import org.joda.time.DateTime

/**
 * Класс-обертка для доступа к настройкам приложения.
 */
object AppPreference_1_0 {

    private const val UPDATE_APP_TIME = "update_app_time"

    private const val HOME_SCHEDULE_DELTA = "home_schedule_delta"
    private const val DISPLAY_SUBGROUP = "schedule_home_subgroup"
    private const val SCHEDULE_SUBGROUP = "schedule_subgroup"

    const val LECTURE_COLOR = "schedule_lecture_color"
    const val SEMINAR_COLOR = "schedule_seminar_color"
    const val LABORATORY_COLOR = "schedule_laboratory_color"
    const val SUBGROUP_A_COLOR = "schedule_subgroup_a_color"
    const val SUBGROUP_B_COLOR = "schedule_subgroup_b_color"

    const val DARK_MODE_SYSTEM_DEFAULT = "pref_system_default"
    const val DARK_MODE_BATTERY_SAVER = "pref_battery_saver"
    const val DARK_MODE_MANUAL = "pref_manual_mode"

    private const val APP_BROWSER = "app_browser"

    const val SCHEDULE_VIEW_VERTICAL = "pref_vertical"
    const val SCHEDULE_VIEW_HORIZONTAL = "pref_horizontal"

    private const val FIREBASE_ANALYTICS = "firebase_analytics"
    private const val FIREBASE_CRASHLYTICS = "firebase_crashlytics"

    private const val SCHEDULE_VIEW_METHOD = "schedule_view_method"
    private const val SCHEDULE_LIMIT = "schedule_view_limit"

    private const val DARK_MODE = "dark_mode"
    private const val MANUAL_MODE = "manual_mode"

    /**
     * Возвращает текущее сохраненное значение режима темной темы.
     *
     * @param context Контекст приложения
     * @return Режим темной темы или значение по умолчанию
     */
    @JvmStatic
    fun currentDarkMode(context: Context): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(DARK_MODE, DARK_MODE_SYSTEM_DEFAULT)
    }

    /**
     * Устанавливает значение режима темной темы.
     *
     * @param context Контекст приложения
     * @param darkMode Режим темной темы для установки
     */
    @JvmStatic
    fun setDarkMode(context: Context, darkMode: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putString(DARK_MODE, darkMode)
            .apply()
    }

    /**
     * Возвращает текущее значение ручного переключателя темной темы.
     *
     * @param context Контекст приложения
     * @return true, если ручной режим включен, иначе false
     */
    @JvmStatic
    fun currentManualMode(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(MANUAL_MODE, false)
    }

    /**
     * Устанавливает значение ручного переключателя темной темы.
     *
     * @param context Контекст приложения
     * @param isDarkModeEnabled true для включения ручного режима, false для выключения
     */
    @JvmStatic
    fun setManualMode(context: Context, isDarkModeEnabled: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putBoolean(MANUAL_MODE, isDarkModeEnabled)
            .apply()
    }

    /**
     * Возвращает значение, должен ли использоваться встроенный браузер.
     *
     * @param context Контекст приложения
     * @return true, если должен использоваться встроенный браузер, иначе false
     */
    @JvmStatic
    fun useAppBrowser(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(APP_BROWSER, true)
    }

    /**
     * Возвращает значение, как должно отображаться расписание.
     *
     * @param context Контекст приложения
     * @return Метод отображения расписания (вертикальный или горизонтальный)
     */
    @JvmStatic
    fun scheduleViewMethod(context: Context): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SCHEDULE_VIEW_METHOD, SCHEDULE_VIEW_HORIZONTAL)
    }

    /**
     * Возвращает значение, должно ли ограничиваться расписание.
     *
     * @param context Контекст приложения
     * @return true, если расписание должно быть ограничено, иначе false
     */
    @JvmStatic
    fun scheduleLimit(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(SCHEDULE_LIMIT, false)
    }

    /**
     * Возвращает true, если необходимо отображать подгруппу на главной.
     *
     * @param context Контекст приложения
     * @return true, если нужно отображать подгруппу, иначе false
     */
    @JvmStatic
    fun displaySubgroup(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(DISPLAY_SUBGROUP, true)
    }

    /**
     * Устанавливает, нужно ли отображать подгруппу на главной.
     *
     * @param context Контекст приложения
     * @param display true для отображения подгруппы, false для скрытия
     */
    @JvmStatic
    fun setDisplaySubgroup(context: Context, display: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putBoolean(DISPLAY_SUBGROUP, display)
            .apply()
    }

    /**
     * Возвращает список цветов для расписания.
     *
     * @param context Контекст приложения
     * @param colorNames Имена цветовых настроек для получения
     * @return Список цветов в формате Int
     */
    @JvmStatic
    fun colors(context: Context, vararg colorNames: String): List<Int> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val colors = ArrayList<Int>()

        for (colorName in colorNames) {
            colors += preferences.getInt(colorName, Color.TRANSPARENT)
        }

        return colors
    }

    /**
     * Возвращает время, когда последний раз проверялось доступность обновлений.
     * Возвращается null, если никогда не проверялось.
     *
     * @param context Контекст приложения
     * @return Время последней проверки обновлений или null
     */
    @JvmStatic
    fun updateAppTime(context: Context): DateTime? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val dateTime = preferences.getString(UPDATE_APP_TIME, null) ?: return null
        return DateTime.parse(dateTime)
    }

    /**
     * Устанавливает время последней проверки обновления приложения.
     *
     * @param context Контекст приложения
     * @param dateTime Время проверки обновлений
     */
    @JvmStatic
    fun setUpdateAppTime(context: Context, dateTime: DateTime) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putString(UPDATE_APP_TIME, dateTime.toString())
            .apply()
    }

    /**
     * Возвращает true, если можно использовать Firebase аналитику для
     * сбора данных об использовании приложения. Иначе false.
     *
     * @param context Контекст приложения
     * @return true, если аналитика разрешена, иначе false
     */
    @JvmStatic
    fun firebaseAnalytics(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(FIREBASE_ANALYTICS, true)
    }

    /**
     * Возвращает true, если можно использовать Firebase crashlytics для
     * сбора ошибок об использовании приложения. Иначе false.
     *
     * @param context Контекст приложения
     * @return true, если crashlytics разрешен, иначе false
     */
    @JvmStatic
    fun firebaseCrashlytics(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(FIREBASE_CRASHLYTICS, true)
    }
}