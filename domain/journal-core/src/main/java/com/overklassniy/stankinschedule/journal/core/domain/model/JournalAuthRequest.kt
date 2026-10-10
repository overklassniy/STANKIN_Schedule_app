package com.overklassniy.stankinschedule.journal.core.domain.model

/**
 * Подготовленный OIDC-запрос авторизации для открытия в WebView.
 *
 * @property url URL страницы авторизации (trusted.stankin.ru) со всеми параметрами.
 * @property state Одноразовое значение state, связанное с code_verifier этого запроса.
 */
data class JournalAuthRequest(
    val url: String,
    val state: String,
)
