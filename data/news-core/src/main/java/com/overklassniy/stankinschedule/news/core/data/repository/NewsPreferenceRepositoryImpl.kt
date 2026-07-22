package com.overklassniy.stankinschedule.news.core.data.repository

import com.overklassniy.stankinschedule.core.domain.settings.PreferenceRepository
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsPreferenceRepository
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Реализация репозитория для работы с настройками новостей.
 * Использует [PreferenceRepository] для сохранения простых данных типа "ключ-значение".
 * В основном используется для хранения времени последнего обновления новостей.
 *
 * @property preference Репозиторий настроек приложения.
 */
class NewsPreferenceRepositoryImpl @Inject constructor(
    private val preference: PreferenceRepository
) : NewsPreferenceRepository {

    /**
     * Обновляет время последней проверки/загрузки новостей для указанного подразделения.
     *
     * @param subdivision ID подразделения новостей.
     * @param time Время обновления ([DateTime]).
     */
    override suspend fun updateNewsDateTime(subdivision: Int, time: DateTime) {
        preference.saveDateTime("news_$subdivision", time)
    }

    /**
     * Получает сохраненное время последнего обновления новостей для указанного подразделения.
     *
     * @param subdivision ID подразделения новостей.
     * @return Время последнего обновления ([DateTime]) или null, если время не было сохранено.
     */
    override suspend fun currentNewsDateTime(subdivision: Int): DateTime? {
        return preference.getDateTime("news_$subdivision")
    }
}