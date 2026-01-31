package com.overklassniy.stankinschedule.journal.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.overklassniy.stankinschedule.journal.core.data.repository.JournalPreferenceImpl.Companion.UPDATE_MARKS_ALLOW
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val JOURNAL_PREFERENCE = "journal_preference"
private val Context.dataStore by preferencesDataStore(name = JOURNAL_PREFERENCE)

/**
 * Реализация интерфейса [JournalPreference] для управления настройками журнала.
 * Использует `DataStore` для сохранения и получения предпочтений пользователя,
 * связанных с функционалом журнала (например, разрешение на обновление оценок).
 *
 * @param context Контекст приложения, необходимый для доступа к DataStore.
 */
class JournalPreferenceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : JournalPreference {

    /**
     * Возвращает поток данных, указывающий, разрешено ли обновление оценок.
     *
     * Алгоритм:
     * 1. Подписывается на изменения в DataStore.
     * 2. Извлекает значение по ключу [UPDATE_MARKS_ALLOW].
     * 3. Если значение не установлено, возвращает false по умолчанию.
     *
     * @return [Flow] с булевым значением: true, если обновление разрешено, иначе false.
     */
    override fun isUpdateMarksAllow(): Flow<Boolean> =
        context.dataStore.data.map { preferences -> preferences[UPDATE_MARKS_ALLOW] ?: false }

    /**
     * Устанавливает разрешение на обновление оценок.
     *
     * Алгоритм:
     * 1. Открывает DataStore для редактирования.
     * 2. Сохраняет переданное значение [allow] по ключу [UPDATE_MARKS_ALLOW].
     *
     * @param allow true, чтобы разрешить обновление, false, чтобы запретить.
     */
    override suspend fun setUpdateMarksAllow(allow: Boolean) {
        context.dataStore.edit { preferences -> preferences[UPDATE_MARKS_ALLOW] = allow }
    }

    companion object {
        /**
         * Ключ для сохранения настройки разрешения обновления оценок в DataStore.
         */
        private val UPDATE_MARKS_ALLOW get() = booleanPreferencesKey("update_marks_allow")
    }
}