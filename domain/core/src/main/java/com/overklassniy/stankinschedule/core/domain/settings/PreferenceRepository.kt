package com.overklassniy.stankinschedule.core.domain.settings

import org.joda.time.DateTime

/**
 * Интерфейс репозитория для работы с персистентным хранилищем настроек (Key-Value storage).
 *
 * Абстрагирует работу с `SharedPreferences` или аналогичными механизмами.
 */
interface PreferenceRepository {

    /**
     * Получает булево значение по ключу.
     *
     * @param key Ключ настройки.
     * @param default Значение по умолчанию, если ключ не найден.
     * @return Сохраненное значение или [default].
     */
    fun getBoolean(key: String, default: Boolean): Boolean

    /**
     * Сохраняет булево значение.
     *
     * @param key Ключ настройки.
     * @param value Значение для сохранения.
     */
    fun saveBoolean(key: String, value: Boolean)

    /**
     * Получает строковое значение по ключу.
     *
     * @param key Ключ настройки.
     * @return Сохраненная строка или null, если значение отсутствует.
     */
    fun getString(key: String): String?

    /**
     * Сохраняет строковое значение.
     *
     * @param key Ключ настройки.
     * @param value Значение для сохранения.
     */
    fun saveString(key: String, value: String)

    /**
     * Получает значение даты и времени по ключу.
     *
     * @param key Ключ настройки.
     * @return Объект [DateTime] или null, если значение отсутствует или не удалось распарсить.
     */
    fun getDateTime(key: String): DateTime?

    /**
     * Сохраняет значение даты и времени.
     *
     * @param key Ключ настройки.
     * @param value Объект [DateTime] для сохранения.
     */
    fun saveDateTime(key: String, value: DateTime)
}