package com.overklassniy.stankinschedule.core.ui.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.core.ui.R
import org.json.JSONException
import retrofit2.HttpException
import java.io.FileNotFoundException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLException

/**
 * Возвращает локализованное описание исключения через ресурсы.
 *
 * @param t Исключение.
 * @return Текстовое описание для пользователя.
 */
fun Context.exceptionDescription(t: Throwable): String {
    val descriptionRes = exceptionDescriptionRes(t)
    return if (descriptionRes != null) {
        getString(descriptionRes)
    } else {
        getString(R.string.ex_unknown)
    }
}

/**
 * Возвращает локализованное описание исключения для Composable-контекста.
 *
 * @param t Исключение.
 * @return Текстовое описание из stringResource.
 */
@Composable
fun exceptionDescription(t: Throwable): String {
    val descriptionRes = exceptionDescriptionRes(t)
    return if (descriptionRes != null) {
        stringResource(descriptionRes)
    } else {
        stringResource(R.string.ex_unknown)
    }
}

/**
 * Подбирает идентификатор строкового ресурса для исключения.
 *
 * @param t Исключение.
 * @return Идентификатор ресурса или null, если нет подходящего.
 */
@StringRes
private fun exceptionDescriptionRes(t: Throwable): Int? {
    return when (t) {
        // Время ожидания сокета истекло
        is SocketTimeoutException -> R.string.ex_socket_timeout

        // Не удалось подключиться к хосту
        is UnknownHostException -> R.string.ex_unknown_host

        // Соединение отклонено
        is ConnectException -> R.string.ex_connection_refused

        // Ошибка SSL/TLS
        is SSLException -> R.string.ex_ssl_error

        // Файл не найден
        is FileNotFoundException -> R.string.ex_file_not_found

        // Ошибка парсинга JSON
        is JSONException -> R.string.ex_parse_error

        // HTTP ошибка (retrofit2)
        is HttpException -> {
            when (t.code()) {
                HttpsURLConnection.HTTP_UNAUTHORIZED -> R.string.ex_failed_unauthorized
                HttpsURLConnection.HTTP_GATEWAY_TIMEOUT -> R.string.ex_socket_timeout
                HttpsURLConnection.HTTP_NOT_FOUND -> R.string.ex_not_found
                HttpsURLConnection.HTTP_FORBIDDEN -> R.string.ex_forbidden
                in 500..599 -> R.string.ex_unknown // Для серверных ошибок используем общий текст
                else -> null
            }
        }

        // Общая ошибка ввода-вывода (после более специфичных)
        is IOException -> R.string.ex_io_error

        else -> null
    }
}