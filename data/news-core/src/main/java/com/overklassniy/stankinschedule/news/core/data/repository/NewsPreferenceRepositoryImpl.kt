package com.overklassniy.stankinschedule.news.core.data.repository

import com.overklassniy.stankinschedule.core.data.preference.PreferenceManager
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsPreferenceRepository
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Реализация репозитория для работы с настройками новостей.
 * Использует [PreferenceManager] для сохранения простых данных типа "ключ-значение".
 * В основном используется для хранения времени последнего обновления новостей.
 *
 * @property preference Менеджер настроек приложения.
 */
class NewsPreferenceRepositoryImpl @Inject constructor(
    private val preference: PreferenceManager
) : NewsPreferenceRepository {

    /**
     * Обновляет время последней проверки/загрузки новостей для указанного подразделения.
     *
     * @param subdivision ID подразделения новостей.
     * @param time Время обновления ([DateTime]).
     */
    override fun updateNewsDateTime(subdivision: Int, time: DateTime) {
        preference.saveDateTime("news_$subdivision", time)
    }

    /**
     * Получает сохраненное время последнего обновления новостей для указанного подразделения.
     *
     * @param subdivision ID подразделения новостей.
     * @return Время последнего обновления ([DateTime]) или null, если время не было сохранено.
     */
    override fun currentNewsDateTime(subdivision: Int): DateTime? {
        return preference.getDateTime("news_$subdivision")
    }
}