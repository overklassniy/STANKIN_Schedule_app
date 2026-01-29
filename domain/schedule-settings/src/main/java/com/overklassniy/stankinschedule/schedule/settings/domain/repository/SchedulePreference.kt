package com.overklassniy.stankinschedule.schedule.settings.domain.repository

import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorType
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс доступа к пользовательским настройкам расписания.
 *
 * Хранит и предоставляет избранное, режим просмотра и цветовую схему пар.
 */
interface SchedulePreference {

    /**
     * Возвращает поток с идентификатором избранного расписания.
     *
     * @return Flow<Long> с ID расписания.
     */
    fun favorite(): Flow<Long>

    /**
     * Устанавливает идентификатор избранного расписания.
     *
     * @param id Идентификатор расписания.
     */
    suspend fun setFavorite(id: Long)

    /**
     * Возвращает поток флага вертикального режима просмотра расписания.
     *
     * @return Flow<Boolean>, где true — вертикальный режим.
     */
    fun isVerticalViewer(): Flow<Boolean>

    /**
     * Устанавливает режим просмотра расписания.
     *
     * @param isVertical Вертикальный режим, если true.
     */
    suspend fun setVerticalViewer(isVertical: Boolean)

    /**
     * Возвращает поток HEX-цвета для указанного типа пары.
     *
     * @param type Тип пары.
     * @return Flow<String> с HEX-значением цвета.
     */
    fun scheduleColor(type: PairColorType): Flow<String>

    /**
     * Возвращает поток цветовой группы для всех типов пар.
     *
     * @return Flow<PairColorGroup> с текущей группой цветов.
     */
    fun scheduleColorGroup(): Flow<PairColorGroup>

    /**
     * Устанавливает HEX-цвет для указанного типа пары.
     *
     * @param hex HEX-строка цвета (например, "#80DEEA").
     * @param type Тип пары.
     */
    suspend fun setScheduleColor(hex: String, type: PairColorType)
}