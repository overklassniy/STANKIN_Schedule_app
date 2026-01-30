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
        return when (throwable) {
            // Ошибки библиотеки парсинга PDF
            is PDFParseException -> {
                when {
                    throwable.message?.contains("password", ignoreCase = true) == true ->
                        context.getString(R.string.error_pdf_password_protected)

                    throwable.message?.contains("open", ignoreCase = true) == true ||
                            throwable.message?.contains("найден", ignoreCase = true) == true ||
                            throwable.message?.contains("found", ignoreCase = true) == true ->
                        context.getString(R.string.error_file_not_found)

                    throwable.message?.contains("valid", ignoreCase = true) == true ||
                            throwable.message?.contains("corrupt", ignoreCase = true) == true ||
                            throwable.message?.contains("корректн", ignoreCase = true) == true ||
                            throwable.message?.contains("поврежд", ignoreCase = true) == true ->
                        context.getString(R.string.error_invalid_pdf)

                    else -> context.getString(R.string.error_pdf_read_failed)
                }
            }

            // Специальная проверка на формат СТАНКИН: отсутствует время в таблице
            is IllegalArgumentException if throwable.message?.contains(
                "Time not found",
                ignoreCase = true
            ) == true ->
                context.getString(R.string.error_not_schedule_pdf)

            // Другие ошибки формата расписания: неизвестные поля, отсутствующие структуры
            is IllegalArgumentException if (
                    throwable.message?.contains("not found", ignoreCase = true) == true ||
                            throwable.message?.contains("Unknown", ignoreCase = true) == true ||
                            throwable.message?.contains("No parse", ignoreCase = true) == true ||
                            throwable.message?.contains("Invalid", ignoreCase = true) == true
                    ) ->
                context.getString(R.string.error_schedule_format)

            // Ошибка ввода-вывода при чтении PDF
            is java.io.IOException -> context.getString(R.string.error_pdf_read_failed)

            // Некорректное состояние при обработке PDF
            is IllegalStateException -> context.getString(R.string.error_invalid_pdf)

            // Неизвестная ошибка
            else -> context.getString(R.string.error_unknown)
        }
    }
}
