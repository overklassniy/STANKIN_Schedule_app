package com.overklassniy.stankinschedule.schedule.settings.domain.repository

import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorType
import kotlinx.coroutines.flow.Flow

interface SchedulePreference {

    fun favorite(): Flow<Long>

    suspend fun setFavorite(id: Long)

    fun isVerticalViewer(): Flow<Boolean>

    suspend fun setVerticalViewer(isVertical: Boolean)

    fun scheduleColor(type: PairColorType): Flow<String>

    fun scheduleColorGroup(): Flow<PairColorGroup>

    suspend fun setScheduleColor(hex: String, type: PairColorType)
}