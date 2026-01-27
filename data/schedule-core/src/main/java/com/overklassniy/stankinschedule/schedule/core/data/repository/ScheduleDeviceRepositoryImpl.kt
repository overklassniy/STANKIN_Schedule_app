package com.overklassniy.stankinschedule.schedule.core.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.overklassniy.stankinschedule.schedule.core.data.api.PairJson
import com.overklassniy.stankinschedule.schedule.core.data.mapper.toJson
import com.overklassniy.stankinschedule.schedule.core.data.mapper.toPairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleDeviceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileNotFoundException
import javax.inject.Inject

/**
 * Реализация репозитория для работы с файлами расписания на устройстве.
 * Позволяет сохранять и загружать расписание из файловой системы.
 *
 * @property context Контекст приложения.
 */
class ScheduleDeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ScheduleDeviceRepository {

    /**
     * Сохраняет модель расписания в файл на устройстве.
     *
     * @param model Модель расписания для сохранения.
     * @param path Путь (URI строка) для сохранения файла.
     * @throws IllegalAccessException Если не удалось получить дескриптор файла.
     */
    override suspend fun saveToDevice(model: ScheduleModel, path: String) {
        val json = model.toJson()

        val contentResolver = context.contentResolver
        contentResolver.openOutputStream(path.toUri()).use { stream ->
            if (stream == null) throw IllegalAccessException("Failed to get file descriptor")

            stream.bufferedWriter().use { writer ->
                Gson().toJson(json, writer)
            }
        }
    }

    /**
     * Загружает модель расписания из файла на устройстве.
     *
     * @param path Путь (URI строка) к файлу.
     * @return Загруженная модель расписания.
     * @throws FileNotFoundException Если файл не найден или не удалось получить имя файла.
     * @throws IllegalAccessException Если не удалось открыть поток чтения.
     */
    override suspend fun loadFromDevice(path: String): ScheduleModel {
        val contentResolver = context.contentResolver

        val uri = path.toUri()
        val scheduleName = uri.extractFileName(contentResolver)?.substringBeforeLast('.')
            ?: throw FileNotFoundException("Failed to get file descriptor")

        val json: List<PairJson> = contentResolver.openInputStream(uri).use { stream ->
            if (stream == null) throw IllegalAccessException("Failed to get file descriptor")

            stream.bufferedReader().use { reader ->
                Gson().fromJson(reader, object : TypeToken<List<PairJson>>() {}.type)
            }
        }

        val pairs = json.map { it.toPairModel() }

        val info = ScheduleInfo(scheduleName)
        val model = ScheduleModel(info)
        pairs.forEach { model.add(it) }

        return model
    }

    /**
     * Извлекает имя файла из URI.
     *
     * @param contentResolver ContentResolver для запроса метаданных.
     * @return Имя файла или null, если не удалось извлечь.
     */
    private fun Uri.extractFileName(contentResolver: ContentResolver): String? {
        return contentResolver.query(this, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (columnIndex >= 0) cursor.getString(columnIndex) else null
        }
    }
}