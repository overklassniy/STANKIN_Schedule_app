package com.overklassniy.stankinschedule.core.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.overklassniy.stankinschedule.core.domain.repository.DeviceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Реализация репозитория для работы с функциями устройства.
 * Предоставляет методы для взаимодействия с файловой системой и другими системными ресурсами.
 *
 * @param context Контекст приложения, необходимый для доступа к ContentResolver
 */
class DeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceRepository {

    /**
     * Извлекает имя файла из указанного пути (URI строки).
     *
     * Алгоритм:
     * 1. Преобразует строковый путь в URI.
     * 2. Использует ContentResolver для запроса метаданных файла.
     *
     * @param path Путь к файлу в виде строки
     * @return Имя файла или null, если имя не удалось определить
     */
    override fun extractFilename(path: String): String? {
        val uri = path.toUri()
        return uri.extractFileName(context.contentResolver)
    }


    /**
     * Расширение для Uri, извлекающее отображаемое имя файла через ContentResolver.
     *
     * Алгоритм:
     * 1. Делает запрос к ContentResolver по данному Uri.
     * 2. Перемещает курсор на первую строку.
     * 3. Ищет индекс колонки DISPLAY_NAME.
     * 4. Возвращает значение из этой колонки.
     *
     * @param contentResolver Резолвер контента для выполнения запроса
     * @return Имя файла или null, если колонка не найдена или запрос не вернул данных
     */
    private fun Uri.extractFileName(contentResolver: ContentResolver): String? {
        // Запрашиваем метаданные по URI, используя use для автоматического закрытия курсора
        return contentResolver.query(this, null, null, null, null)?.use { cursor ->
            // Переходим к первой записи результата
            cursor.moveToFirst()

            // Получаем индекс колонки с отображаемым именем
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            // Если колонка найдена, возвращаем имя, иначе null
            if (columnIndex >= 0) cursor.getString(columnIndex) else null
        }
    }
}