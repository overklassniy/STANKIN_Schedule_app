package com.overklassniy.stankinschedule.core.domain.settings

import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime

/**
 * Интерфейс репозитория для работы с персистентным хранилищем настроек (Key-Value storage).
 *
 * Абстрагирует работу с DataStore или аналогичными механизмами.
 * suspend-методы выполняют разовые запросы, Flow-методы предоставляют
 * реактивное наблюдение за изменениями значений.
 */
interface PreferenceRepository {

    /**
     * Получает булево значение по ключу.
     *
     * @param key Ключ настройки.
     * @param default Значение по умолчанию, если ключ не найден.
     * @return Сохраненное значение или [default].
     */
    suspend fun getBoolean(key: String, default: Boolean): Boolean

    /**
     * Сохраняет булево значение.
     *
     * @param key Ключ настройки.
     * @param value Значение для сохранения.
     */
    suspend fun saveBoolean(key: String, value: Boolean)

    /**
     * Получает строковое значение по ключу.
     *
     * @param key Ключ настройки.
     * @return Сохраненная строка или null, если значение отсутствует.
     */
    suspend fun getString(key: String): String?

    /**
     * Сохраняет строковое значение.
     *
     * @param key Ключ настройки.
     * @param value Значение для сохранения.
     */
    suspend fun saveString(key: String, value: String)

    /**
     * Возвращает Flow для наблюдения за изменениями строкового значения по ключу.
     *
     * @param key Ключ настройки.
     * @return Flow, эмитящий сохраненную строку или null при каждом изменении.
     */
    fun getStringFlow(key: String): Flow<String?>

    /**
     * Получает значение даты и времени по ключу.
     *
     * @param key Ключ настройки.
     * @return Объект [DateTime] или null, если значение отсутствует или не удалось распарсить.
     */
    suspend fun getDateTime(key: String): DateTime?

    /**
     * Сохраняет значение даты и времени.
     *
     * @param key Ключ настройки.
     * @param value Объект [DateTime] для сохранения.
     */
    suspend fun saveDateTime(key: String, value: DateTime)
}