package com.overklassniy.stankinschedule.journal.core.domain.model

/**
 * Токен доступа к API личного кабинета (lko-api.stankin.ru).
 *
 * Сервер выдает только access token (refresh-токен не выдается),
 * поэтому после истечения срока требуется повторная OIDC-авторизация.
 *
 * @property accessToken Значение access token (JWT).
 * @property obtainedAtMillis Время получения токена в миллисекундах (System.currentTimeMillis).
 */
data class JournalAuthToken(
    val accessToken: String,
    val obtainedAtMillis: Long,
) {

    /**
     * Проверяет, истек ли срок действия токена с учетом запаса [MARGIN_MILLIS].
     *
     * @param nowMillis Текущее время в миллисекундах.
     * @return `true`, если токен считается истекшим.
     */
    fun isExpired(nowMillis: Long): Boolean {
        return nowMillis - obtainedAtMillis >= TTL_MILLIS - MARGIN_MILLIS
    }

    companion object {

        /** Время жизни токена по данным сервера (~10 часов). */
        private const val TTL_MILLIS: Long = 10 * 60 * 60 * 1000L

        /** Запас по времени, чтобы не использовать токен на грани истечения. */
        private const val MARGIN_MILLIS: Long = 5 * 60 * 1000L
    }
}
