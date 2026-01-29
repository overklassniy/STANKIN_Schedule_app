package com.overklassniy.stankinschedule.schedule.viewer.data.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.viewer.data.mapper.toViewPair
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import org.joda.time.LocalDate

/**
 * Источник данных для пейджинга расписания.
 *
 * Загружает данные расписания постранично (по дням) для отображения в списке.
 *
 * @property schedule Модель расписания, из которой берутся данные.
 * @property isDebug Флаг режима отладки для логирования.
 */
class ScheduleViewerSource(
    private val schedule: ScheduleModel,
    private val isDebug: Boolean,
) : PagingSource<LocalDate, ScheduleViewDay>() {

    private val startDate = schedule.startDate()
    private val endDate = schedule.endDate()

    /**
     * Получает ключ обновления для текущего состояния пейджинга.
     *
     * Используется для восстановления позиции списка после инвалидации данных.
     *
     * @param state Текущее состояние пейджинга.
     * @return Ключ (дата) для перезагрузки данных или null.
     */
    override fun getRefreshKey(state: PagingState<LocalDate, ScheduleViewDay>): LocalDate? {
        if (isDebug) {
            Log.d("ScheduleViewSourceLog", "getRefreshKey: $state")
        }

        val position = state.anchorPosition
        if (position != null) {
            state.pages.getOrNull(position)
        }
        return null
    }

    /**
     * Загружает страницу данных.
     *
     * Определяет диапазон дат для загрузки и извлекает пары из модели расписания.
     *
     * @param params Параметры загрузки (ключ, размер страницы).
     * @return Результат загрузки [LoadResult] (страница с данными или ошибка).
     */
    override suspend fun load(
        params: LoadParams<LocalDate>
    ): LoadResult<LocalDate, ScheduleViewDay> {
        val date = params.key ?: LocalDate.now()
        val loadSize = params.loadSize

        if (startDate == null && endDate == null) {
            return LoadResult.Page(listOf(), null, null)
        }

        val nextDay = nextDay(date, loadSize)
        val prevDay = prevDay(date, loadSize)

        if (isDebug) {
            Log.d(
                "ScheduleViewSourceLog",
                "Load view data: " +
                        "${prevDay?.toString("dd.MM.yyyy")} " +
                        "<- ${date.toString("dd.MM.yyyy")} -> " +
                        "${nextDay?.toString("dd.MM.yyyy")}. Total: $loadSize"
            )
        }

        return LoadResult.Page(
            loadDays(date, nextDay),
            prevDay,
            nextDay
        )
    }

    /**
     * Загружает список дней с парами в заданном диапазоне.
     *
     * @param from Дата начала диапазона.
     * @param to Дата окончания диапазона.
     * @return Список дней расписания [ScheduleViewDay].
     */
    private fun loadDays(from: LocalDate, to: LocalDate?): List<ScheduleViewDay> {
        var begin = from
        val end = to ?: endDate!!

        if (isDebug) {
            Log.d(
                "ScheduleViewSourceLog",
                "load: ${begin.toString("dd.MM.yyyy")} " +
                        "<--> ${end.toString("dd.MM.yyyy")}"
            )
        }

        val result = ArrayList<ScheduleViewDay>()
        while (begin < end) {
            result.add(
                ScheduleViewDay(
                    pairs = schedule.pairsByDate(begin).map { it.toViewPair() },
                    day = begin
                )
            )
            begin = begin.plusDays(1)
        }

        if (isDebug) {
            Log.d("ScheduleViewSourceLog", "loaded = ${result.size}")
        }

        return result
    }

    /**
     * Вычисляет дату следующей страницы.
     *
     * @param currentDate Текущая дата (ключ).
     * @param pageSize Размер страницы (количество дней).
     * @return Дата следующей страницы или null.
     */
    private fun nextDay(currentDate: LocalDate, pageSize: Int): LocalDate? {
        return currentDate.plusDays(pageSize)
    }

    /**
     * Вычисляет дату предыдущей страницы.
     *
     * @param currentDate Текущая дата (ключ).
     * @param pageSize Размер страницы (количество дней).
     * @return Дата предыдущей страницы или null.
     */
    private fun prevDay(currentDate: LocalDate, pageSize: Int): LocalDate? {
        return currentDate.minusDays(pageSize)
    }
}