package com.overklassniy.stankinschedule.schedule.table.domain.repository

import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri

/**
 * Провайдер публичного доступа: создание URI и экспорт PDF/Bitmap.
 */
interface AndroidPublicProvider {

    /**
     * Создаёт URI для сохранения изображения.
     *
     * @param name Имя файла.
     * @param bitmap Изображение.
     * @return Созданный [Uri].
     */
    fun createUri(name: String, bitmap: Bitmap): Uri

    /**
     * Создаёт URI для сохранения PDF-документа.
     *
     * @param name Имя файла.
     * @param pdf Документ PDF.
     * @return Созданный [Uri].
     */
    fun createUri(name: String, pdf: PdfDocument): Uri

    /**
     * Экспортирует PDF в указанный URI.
     *
     * @param pdf Документ PDF.
     * @param uri Целевой URI.
     */
    fun exportPdf(pdf: PdfDocument, uri: Uri)

    /**
     * Экспортирует изображение в указанный URI.
     *
     * @param bitmap Изображение.
     * @param uri Целевой URI.
     */
    fun exportBitmap(bitmap: Bitmap, uri: Uri)

}