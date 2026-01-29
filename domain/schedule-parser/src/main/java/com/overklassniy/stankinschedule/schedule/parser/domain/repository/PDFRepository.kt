package com.overklassniy.stankinschedule.schedule.parser.domain.repository

import android.graphics.Bitmap
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParseDetail

/**
 * Интерфейс репозитория для работы с PDF файлами расписания.
 */
interface PDFRepository {

    /**
     * Парсит структуру PDF файла, извлекая текстовые блоки.
     *
     * @param path Путь к файлу.
     * @param multilineTextThreshold Порог объединения строк (коэффициент высоты строки), используется для склеивания многострочного текста.
     * @return Детализированный результат парсинга [ParseDetail] (список ячеек).
     */
    suspend fun parsePDF(
        path: String,
        multilineTextThreshold: Float = 1f
    ): ParseDetail

    /**
     * Рендерит страницу PDF файла в изображение.
     *
     * @param path Путь к файлу.
     * @return Bitmap изображения.
     */
    suspend fun renderPDF(path: String): Bitmap
}