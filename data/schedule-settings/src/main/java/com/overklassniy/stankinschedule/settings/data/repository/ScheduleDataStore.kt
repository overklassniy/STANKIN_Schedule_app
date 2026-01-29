package com.overklassniy.stankinschedule.settings.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorType
import com.overklassniy.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val SCHEDULE_PREFERENCE = "schedule_preference"
private val Context.dataStore by preferencesDataStore(name = SCHEDULE_PREFERENCE)

/**
 * Реализация хранилища настроек расписания на основе DataStore.
 *
 * Позволяет сохранять и получать пользовательские настройки, такие как
 * избранное расписание, режим просмотра и цвета занятий.
 *
 * @property context Контекст приложения.
 */
class ScheduleDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) : SchedulePreference {

    /**
     * Получает идентификатор избранного расписания.
     *
     * @return Flow с ID избранного расписания или -1, если не выбрано.
     */
    override fun favorite(): Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[FAVORITE_SCHEDULE_ID] ?: -1 }

    /**
     * Устанавливает или снимает отметку избранного расписания.
     *
     * Если переданный ID совпадает с текущим избранным, то отметка снимается (значение становится -1).
     *
     * @param id Идентификатор расписания.
     */
    override suspend fun setFavorite(id: Long) {
        context.dataStore.edit { preferences ->
            val lastId = preferences[FAVORITE_SCHEDULE_ID]
            preferences[FAVORITE_SCHEDULE_ID] = if (lastId != id) id else -1
        }
    }

    /**
     * Проверяет, включен ли вертикальный режим просмотра расписания.
     *
     * @return Flow с boolean значением (true - вертикальный, false - горизонтальный).
     */
    override fun isVerticalViewer(): Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[VERTICAL_VIEWER] ?: false }

    /**
     * Устанавливает режим просмотра расписания.
     *
     * @param isVertical true для вертикального режима, false для горизонтального.
     */
    override suspend fun setVerticalViewer(isVertical: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VERTICAL_VIEWER] = isVertical
        }
    }

    /**
     * Получает цвет для указанного типа занятия.
     *
     * @param type Тип занятия [PairColorType].
     * @return Flow со строковым представлением цвета (HEX).
     */
    override fun scheduleColor(type: PairColorType): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[keyForColorType(type)] ?: type.hex
        }
    }

    /**
     * Получает группу цветов для всех типов занятий.
     *
     * @return Flow с объектом [PairColorGroup], содержащим цвета для всех типов пар.
     */
    override fun scheduleColorGroup(): Flow<PairColorGroup> {
        return context.dataStore.data.map { preferences ->
            PairColorGroup(
                lectureColor = preferences.colorOrDefault(PairColorType.Lecture),
                seminarColor = preferences.colorOrDefault(PairColorType.Seminar),
                laboratoryColor = preferences.colorOrDefault(PairColorType.Laboratory),
                subgroupAColor = preferences.colorOrDefault(PairColorType.SubgroupA),
                subgroupBColor = preferences.colorOrDefault(PairColorType.SubgroupB),
            )
        }
    }

    /**
     * Сохраняет цвет для указанного типа занятия.
     *
     * @param hex Строковое представление цвета (HEX).
     * @param type Тип занятия [PairColorType].
     */
    override suspend fun setScheduleColor(hex: String, type: PairColorType) {
        context.dataStore.edit { preferences -> preferences[keyForColorType(type)] = hex }
    }

    private fun androidx.datastore.preferences.core.Preferences.colorOrDefault(type: PairColorType): String {
        return this[keyForColorType(type)] ?: type.hex
    }

    companion object {

        private fun keyForColorType(type: PairColorType): androidx.datastore.preferences.core.Preferences.Key<String> {
            return when (type) {
                PairColorType.Lecture -> stringPreferencesKey("schedule_lecture_color")
                PairColorType.Seminar -> stringPreferencesKey("schedule_seminar_color")
                PairColorType.Laboratory -> stringPreferencesKey("schedule_laboratory_color")
                PairColorType.SubgroupA -> stringPreferencesKey("schedule_subgroup_a_color")
                PairColorType.SubgroupB -> stringPreferencesKey("schedule_subgroup_b_color")
            }
        }

        private val FAVORITE_SCHEDULE_ID
            get() = androidx.datastore.preferences.core.longPreferencesKey(
                "favorite_schedule_id"
            )

        private val VERTICAL_VIEWER
            get() = androidx.datastore.preferences.core.booleanPreferencesKey(
                "schedule_vertical_viewer"
            )
    }
}