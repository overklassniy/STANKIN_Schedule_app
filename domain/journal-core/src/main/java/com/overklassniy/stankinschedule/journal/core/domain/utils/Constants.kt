package com.overklassniy.stankinschedule.journal.core.domain.utils

object Constants {
    /**
     * Базовый адрес API личного кабинета студента.
     */
    const val JOURNAL_API_URL = "https://lko-api.stankin.ru/"

    /**
     * Адрес веб-версии личного кабинета студента.
     */
    const val LKO_WEB_URL = "https://lko.stankin.ru"

    /**
     * Адрес OIDC-провайдера авторизации.
     */
    const val AUTH_URL = "https://trusted.stankin.ru"

    /**
     * Идентификатор клиента личного кабинета студента.
     */
    const val CLIENT_ID = "5BH656uN1bCAqZwx0Ze1BJ"

    /**
     * Redirect URI, зарегистрированный для клиента личного кабинета.
     * В приложении перехватывается WebView до фактической загрузки страницы.
     */
    const val REDIRECT_URI = "$LKO_WEB_URL/callback"

    /**
     * Запрашиваемые OIDC-scope при авторизации.
     */
    const val AUTH_SCOPE = "openid profile email"
}
