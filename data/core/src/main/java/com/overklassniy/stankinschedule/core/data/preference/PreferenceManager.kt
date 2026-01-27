package com.overklassniy.stankinschedule.core.data.preference

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.overklassniy.stankinschedule.core.domain.settings.PreferenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Реализация репозитория настроек, использующая SharedPreferences.
 * Предоставляет методы для сохранения и получения примитивных типов данных и даты.
 *
 * @param context Контекст приложения, необходимый для доступа к SharedPreferences
 */
class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context,
) : PreferenceRepository {

    // Стандартные SharedPreferences приложения
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Получает логическое значение из настроек.
     *
     * @param key Ключ настройки
     * @param default Значение по умолчанию, если ключ не найден
     * @return Сохраненное значение или значение по умолчанию
     */
    override fun getBoolean(key: String, default: Boolean): Boolean {
        return preferences.getBoolean(key, default)
    }

    /**
     * Сохраняет логическое значение в настройки.
     * Использует асинхронное сохранение (apply) через расширение edit.
     *
     * @param key Ключ настройки
     * @param value Значение для сохранения
     */
    override fun saveBoolean(key: String, value: Boolean) {
        preferences.edit { putBoolean(key, value) }
    }

    /**
     * Получает строковое значение из настроек.
     *
     * @param key Ключ настройки
     * @return Сохраненная строка или null, если значение не найдено
     */
    override fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    /**
     * Сохраняет строковое значение в настройки.
     *
     * @param key Ключ настройки
     * @param value Значение для сохранения
     */
    override fun saveString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    /**
     * Получает объект даты и времени из настроек.
     * Дата хранится в виде строки (ISO 8601).
     *
     * @param key Ключ настройки
     * @return Объект [DateTime] или null, если значение отсутствует или не удалось распарсить
     */
    override fun getDateTime(key: String): DateTime? {
        val dateString = preferences.getString(key, null) ?: return null
        return DateTime.parse(dateString)
    }

    /**
     * Сохраняет объект даты и времени в настройки.
     * Дата преобразуется в строку перед сохранением.
     *
     * @param key Ключ настройки
     * @param value Объект [DateTime] для сохранения
     */
    override fun saveDateTime(key: String, value: DateTime) {
        preferences.edit { putString(key, value.toString()) }
    }
}
