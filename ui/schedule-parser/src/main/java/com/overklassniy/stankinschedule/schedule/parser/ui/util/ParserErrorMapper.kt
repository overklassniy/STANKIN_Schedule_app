package com.overklassniy.stankinschedule.schedule.parser.ui.util

import android.content.Context
import com.overklassniy.stankinschedule.schedule.parser.domain.exceptions.PDFParseException
import com.overklassniy.stankinschedule.schedule.parser.ui.R

/**
 * Утилита для преобразования исключений парсера в понятные пользователю сообщения.
 */
object ParserErrorMapper {
    
    /**
     * Преобразует исключение в локализованное сообщение об ошибке.
     *
     * @param context Контекст для доступа к строковым ресурсам
     * @param throwable Исключение для преобразования
     * @return Локализованное сообщение об ошибке
     */
    fun getErrorMessage(context: Context, throwable: Throwable): String {
        return when {
            // PDFParseException из data слоя
            throwable is PDFParseException -> {
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
            
            // Ошибка "Time not found" - файл не является расписанием СТАНКИН
            throwable is IllegalArgumentException && 
            throwable.message?.contains("Time not found", ignoreCase = true) == true ->
                context.getString(R.string.error_not_schedule_pdf)
            
            // Другие ошибки парсинга формата расписания
            throwable is IllegalArgumentException && (
                throwable.message?.contains("not found", ignoreCase = true) == true ||
                throwable.message?.contains("Unknown", ignoreCase = true) == true ||
                throwable.message?.contains("No parse", ignoreCase = true) == true ||
                throwable.message?.contains("Invalid", ignoreCase = true) == true
            ) ->
                context.getString(R.string.error_schedule_format)
            
            // IOException при чтении PDF
            throwable is java.io.IOException ->
                context.getString(R.string.error_pdf_read_failed)
            
            // IllegalStateException от PDFBox
            throwable is IllegalStateException ->
                context.getString(R.string.error_invalid_pdf)
            
            // Неизвестная ошибка
            else -> context.getString(R.string.error_unknown)
        }
    }
}
