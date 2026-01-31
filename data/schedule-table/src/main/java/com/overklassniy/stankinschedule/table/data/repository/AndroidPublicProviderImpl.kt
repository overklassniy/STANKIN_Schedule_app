package com.overklassniy.stankinschedule.table.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.overklassniy.stankinschedule.schedule.table.domain.repository.AndroidPublicProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/**
 * Реализация провайдера для работы с файлами и URI в Android.
 *
 * Используется для создания временных файлов в кэше и получения URI для шаринга,
 * а также для экспорта файлов (PDF, изображения) по заданному URI.
 *
 * @property context Контекст приложения.
 */
class AndroidPublicProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AndroidPublicProvider {

    /**
     * Получает authority для FileProvider на основе applicationId приложения.
     * Это важно для корректной работы в debug/release сборках.
     */
    private val fileProviderAuthority: String
        get() = "${context.packageName}.provider"

    /**
     * Создает URI для переданного изображения (Bitmap).
     *
     * Сохраняет изображение во временную папку кэша и возвращает Content URI через FileProvider.
     *
     * @param name Имя файла (без расширения).
     * @param bitmap Изображение для сохранения.
     * @return URI файла, доступный для других приложений.
     */
    override fun createUri(name: String, bitmap: Bitmap): Uri {
        val folder = getSharedFolder()

        val file = File(folder, "$name.jpeg")
        file.outputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        }

        return FileProvider.getUriForFile(context, fileProviderAuthority, file)
    }

    /**
     * Создает URI для переданного PDF документа.
     *
     * Сохраняет документ во временную папку кэша и возвращает Content URI через FileProvider.
     *
     * @param name Имя файла (без расширения).
     * @param pdf PDF документ для сохранения.
     * @return URI файла, доступный для других приложений.
     */
    override fun createUri(name: String, pdf: PdfDocument): Uri {
        val folder = getSharedFolder()

        val file = File(folder, "$name.pdf")
        file.outputStream().use { stream ->
            pdf.writeTo(stream)
        }

        return FileProvider.getUriForFile(context, fileProviderAuthority, file)
    }

    /**
     * Экспортирует PDF документ по указанному URI.
     *
     * Записывает содержимое PDF документа в поток вывода, открытый по URI.
     *
     * @param pdf PDF документ для экспорта.
     * @param uri URI назначения (куда сохранять файл).
     * @throws IllegalAccessException Если не удалось открыть поток вывода.
     */
    override fun exportPdf(pdf: PdfDocument, uri: Uri) {
        val contentResolver = context.contentResolver
        contentResolver.openOutputStream(uri).use { stream ->
            if (stream == null) throw IllegalAccessException("Failed to get file descriptor")
            pdf.writeTo(stream)
        }
    }

    /**
     * Экспортирует изображение (Bitmap) по указанному URI.
     *
     * Записывает сжатое изображение (JPEG) в поток вывода, открытый по URI.
     *
     * @param bitmap Изображение для экспорта.
     * @param uri URI назначения.
     * @throws IllegalAccessException Если не удалось открыть поток вывода.
     */
    override fun exportBitmap(bitmap: Bitmap, uri: Uri) {
        val contentResolver = context.contentResolver
        contentResolver.openOutputStream(uri).use { stream ->
            if (stream == null) throw IllegalAccessException("Failed to get file descriptor")
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        }
    }

    /**
     * Получает папку для временных файлов шаринга.
     *
     * Очищает предыдущие файлы в этой папке перед использованием.
     *
     * @return Объект [File], указывающий на папку.
     */
    private fun getSharedFolder(): File {
        val folder = File(context.cacheDir, "shared_data")
        folder.deleteRecursively()
        folder.mkdirs()

        return folder
    }

}