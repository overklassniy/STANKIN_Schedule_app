package com.overklassniy.stankinschedule.schedule.parser.ui.util

import android.content.Context
import com.overklassniy.stankinschedule.schedule.parser.domain.exceptions.PDFParseException
import com.overklassniy.stankinschedule.schedule.parser.ui.R

/**
 * Маппер ошибок парсинга PDF в человекочитаемые сообщения.
 */
object ParserErrorMapper {

    /**
     * Возвращает локализованное сообщение ошибки для экрана парсинга.
     *
     * @param context Контекст для доступа к ресурсам.
     * @param throwable Исключение, возникшее при парсинге/чтении PDF.
     * @return Текст сообщения для пользователя.
     */
    fun getErrorMessage(context: Context, throwable: Throwable): String {
        return when {
            throwable is PDFParseException -> pdfParseErrorMessage(context, throwable)
            throwable is IllegalArgumentException && throwable.hasMessage("Time not found") ->
                context.getString(R.string.error_not_schedule_pdf)

            throwable is IllegalArgumentException && throwable.hasMessage(
                "not found", "Unknown", "No parse", "Invalid"
            ) -> context.getString(R.string.error_schedule_format)

            throwable is java.io.IOException -> context.getString(R.string.error_pdf_read_failed)
            throwable is IllegalStateException -> context.getString(R.string.error_invalid_pdf)
            else -> context.getString(R.string.error_unknown)
        }
    }

    /**
     * Подбирает сообщение для [PDFParseException] по ключевым словам в тексте ошибки.
     */
    private fun pdfParseErrorMessage(context: Context, throwable: PDFParseException): String {
        return when {
            throwable.hasMessage("password") ->
                context.getString(R.string.error_pdf_password_protected)

            throwable.hasMessage("open", "найден", "found") ->
                context.getString(R.string.error_file_not_found)

            throwable.hasMessage("valid", "corrupt", "корректн", "поврежд") ->
                context.getString(R.string.error_invalid_pdf)

            else -> context.getString(R.string.error_pdf_read_failed)
        }
    }

    /**
     * Проверяет, содержит ли сообщение исключения хотя бы одно из ключевых слов.
     */
    private fun Throwable.hasMessage(vararg keywords: String): Boolean {
        return keywords.any { message?.contains(it, ignoreCase = true) == true }
    }
}
