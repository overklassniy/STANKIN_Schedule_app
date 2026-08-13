package com.overklassniy.stankinschedule.core.data.preference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.overklassniy.stankinschedule.core.domain.settings.PreferenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.joda.time.DateTime
import javax.inject.Inject

private const val APP_PREFERENCE = "app_preference"
private val Context.appDataStore by preferencesDataStore(name = APP_PREFERENCE)

/**
 * Реализация репозитория настроек на основе DataStore.
 *
 * Предоставляет методы для сохранения и получения примитивных типов данных и даты.
 * Все методы являются suspend, так как DataStore выполняет операции асинхронно.
 *
 * @param context Контекст приложения, необходимый для доступа к DataStore.
 */
class AppPreferenceDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) : PreferenceRepository {

    override suspend fun getBoolean(key: String, default: Boolean): Boolean {
        return context.appDataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)] ?: default
        }.first()
    }

    override suspend fun saveBoolean(key: String, value: Boolean) {
        context.appDataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    override suspend fun getString(key: String): String? {
        return context.appDataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)]
        }.first()
    }

    override suspend fun saveString(key: String, value: String) {
        context.appDataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    override fun getStringFlow(key: String): Flow<String?> {
        return context.appDataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)]
        }
    }

    override suspend fun getDateTime(key: String): DateTime? {
        val dateString = getString(key) ?: return null
        return DateTime.parse(dateString)
    }

    override suspend fun saveDateTime(key: String, value: DateTime) {
        saveString(key, value.toString())
    }
}
