package com.overklassniy.stankinschedule.journal.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа сервера на обмен кода авторизации на токен.
 * Эндпоинт: `auth/gettoken`.
 *
 * @param accessToken Access token (JWT) для запросов к API личного кабинета.
 * @param tokenType Тип токена (обычно "Bearer").
 */
class TokenResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("tokenType") val tokenType: String? = null,
)
