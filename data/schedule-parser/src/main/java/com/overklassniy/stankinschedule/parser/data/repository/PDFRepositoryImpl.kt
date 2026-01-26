package com.overklassniy.stankinschedule.parser.data.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import com.overklassniy.stankinschedule.schedule.parser.domain.exceptions.PDFParseException
import com.overklassniy.stankinschedule.schedule.parser.domain.model.CellBound
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParseDetail
import com.overklassniy.stankinschedule.schedule.parser.domain.repository.PDFRepository
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import com.tom_roush.pdfbox.rendering.PDFRenderer
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

/**
 * Реализация репозитория для работы с PDF файлами расписания.
 */
class PDFRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
    ) : PDFRepository {

    /**
     * Парсит PDF файл и извлекает ячейки с текстом.
     *
     * @param path Путь к PDF файлу
     * @param multilineTextThreshold Порог для объединения многострочного текста
     * @return Детали парсинга с ячейками
     * @throws PDFParseException если не удалось открыть или распарсить файл
     */
    override suspend fun parsePDF(
        path: String,
        multilineTextThreshold: Float
    ): ParseDetail {
        PDFBoxResourceLoader.init(context)

        return openInputStream(path).use { stream ->
            if (stream == null) throw PDFParseException.fileNotFound(path)
            try {
                ParseDetail(
                    scheduleName = "",
                    cells = import(stream, multilineTextThreshold)
                )
            } catch (e: InvalidPasswordException) {
                throw PDFParseException.passwordProtected()
            } catch (e: IOException) {
                throw PDFParseException.invalidPDF(e)
            } catch (e: IllegalStateException) {
                throw PDFParseException.invalidPDF(e)
            } catch (e: Exception) {
                throw PDFParseException.parsingError(e)
            }
        }
    }

    /**
     * Рендерит первую страницу PDF файла в изображение.
     *
     * @param path Путь к PDF файлу
     * @return Bitmap изображение первой страницы
     * @throws PDFParseException если не удалось открыть или рендерить файл
     */
    override suspend fun renderPDF(path: String): Bitmap {
        PDFBoxResourceLoader.init(context)

        return openInputStream(path).use { stream ->
            if (stream == null) throw PDFParseException.fileNotFound(path)
            try {
                render(stream)
            } catch (e: InvalidPasswordException) {
                throw PDFParseException.passwordProtected()
            } catch (e: IOException) {
                throw PDFParseException.invalidPDF(e)
            } catch (e: IllegalStateException) {
                throw PDFParseException.invalidPDF(e)
            } catch (e: Exception) {
                throw PDFParseException.parsingError(e)
            }
        }
    }

    /**
     * Открывает InputStream для файла по пути.
     *
     * @param path Путь к файлу (может быть content://, file:// или обычный путь)
     * @return InputStream или null, если файл не существует
     */
    private fun openInputStream(path: String): InputStream? {
        return if (path.startsWith("content://") || path.startsWith("file://")) {
            context.contentResolver.openInputStream(path.toUri())
        } else {
            val file = File(path)
            if (file.exists()) FileInputStream(file) else null
        }
    }

    /**
     * Рендерит первую страницу PDF документа в изображение.
     *
     * @param pdf InputStream PDF документа
     * @return Bitmap изображение первой страницы
     */
    fun render(pdf: InputStream): Bitmap {
        return PDDocument.load(pdf).use { document ->
            val renderer = PDFRenderer(document)
            renderer.renderImageWithDPI(0, 300f)
        }
    }

    /**
     * Импортирует текст из PDF документа и извлекает ячейки.
     *
     * @param pdf InputStream PDF документа
     * @param multilineTextThreshold Порог для объединения многострочного текста
     * @return Список ячеек с текстом и координатами
     */
    fun import(pdf: InputStream, multilineTextThreshold: Float): List<CellBound> {
        return PDDocument.load(pdf).use { document ->
            val stripper = StringBoundStripper()
            val bounds = stripper.processStringBounds(document)
            mergeStringBounds(bounds, multilineTextThreshold)
        }
    }

    /**
     * Объединяет строки текста в ячейки на основе близости и одинакового шрифта.
     *
     * @param bounds Список границ строк текста
     * @param multilineTextThreshold Порог для объединения многострочного текста
     * @return Список объединенных ячеек
     */
    private fun mergeStringBounds(
        bounds: List<StringBoundStripper.StringBound>,
        multilineTextThreshold: Float
    ): List<CellBound> {
        val cells = mutableListOf<CellBound>()

        for (bound in bounds) {
            val cell = cells.find { cell ->
                abs(cell.maxFontHeight - bound.h) < 0.1f &&
                        (bound.y - (cell.y + cell.h)) < cell.maxFontHeight * multilineTextThreshold &&
                        abs(cell.x - bound.x) < 1f
            }
            cells += if (cell != null) {
                cells.remove(cell)
                CellBound(
                    text = cell.text + " " + bound.text,
                    x = cell.x,
                    y = cell.y,
                    h = (bound.y - cell.y) + bound.h,
                    w = max(cell.w, bound.w),
                    maxFontHeight = cell.maxFontHeight
                )
            } else {
                CellBound(
                    text = bound.text,
                    x = bound.x,
                    y = bound.y,
                    h = bound.h,
                    w = bound.w,
                    maxFontHeight = bound.h
                )
            }
        }

        return cells
    }

    /**
     * Класс для извлечения текста и его позиций из PDF документа.
     */
    private class StringBoundStripper : PDFTextStripper() {

        private var blocks = mutableListOf<StringBound>()

        init {
            sortByPosition = true
        }

        /**
         * Обрабатывает документ и извлекает границы всех строк текста.
         *
         * @param document PDF документ
         * @return Список границ строк текста с координатами
         */
        fun processStringBounds(document: PDDocument): List<StringBound> {
            blocks = mutableListOf()

            val dummy = OutputStreamWriter(ByteArrayOutputStream())
            writeText(document, dummy)

            return blocks
        }

        /**
         * Переопределенный метод для извлечения позиций текста.
         *
         * @param text Текст строки
         * @param textPositions Список позиций символов в строке
         */
        override fun writeString(
            text: String,
            textPositions: MutableList<TextPosition>
        ) {
            val x = textPositions.minOf { pos -> pos.xDirAdj }
            val y = textPositions.minOf { pos -> pos.yDirAdj }
            val h = textPositions.maxOf { pos -> pos.heightDir }
            val w = textPositions.sumOf { pos -> pos.widthDirAdj.toDouble() }.toFloat()

            blocks += StringBound(text, x, y, h, w)
        }

        /**
         * Класс для хранения границ строки текста с координатами.
         *
         * @param text Текст строки
         * @param x X координата начала
         * @param y Y координата начала
         * @param h Высота строки
         * @param w Ширина строки
         */
        class StringBound(
            val text: String,
            val x: Float,
            val y: Float,
            val h: Float,
            val w: Float
        )
    }
}