package com.overklassniy.stankinschedule.schedule.settings.domain.usecase

import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase настроек расписания.
 *
 * Предоставляет доступ к предпочтениям пользователя: избранное расписание,
 * режим просмотра и цветовая схема пар.
 */
class ScheduleSettingsUseCase @Inject constructor(
    private val preference: SchedulePreference,
) {
    /**
     * Устанавливает идентификатор избранного расписания.
     *
     * @param id Идентификатор расписания.
     */
    suspend fun setFavorite(id: Long) = preference.setFavorite(id)

    /**
     * Возвращает поток с идентификатором избранного расписания.
     *
     * @return Flow с ID расписания.
     */
    fun favorite(): Flow<Long> = preference.favorite()

    /**
     * Возвращает поток флага вертикального режима просмотра расписания.
     *
     * @return Flow<Boolean>, где true — вертикальный режим.
     */
    fun isVerticalViewer(): Flow<Boolean> = preference.isVerticalViewer()

    /**
     * Возвращает поток групповых цветов для типов пар.
     *
     * @return Flow<PairColorGroup> с текущей цветовой схемой.
     */
    fun pairColorGroup(): Flow<PairColorGroup> = preference.scheduleColorGroup()
}