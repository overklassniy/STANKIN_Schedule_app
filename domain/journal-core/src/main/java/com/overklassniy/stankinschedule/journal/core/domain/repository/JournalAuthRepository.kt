package com.overklassniy.stankinschedule.journal.core.domain.repository

import com.overklassniy.stankinschedule.journal.core.domain.model.JournalAuthRequest
import com.overklassniy.stankinschedule.journal.core.domain.model.JournalAuthToken

/**
 * Репозиторий OIDC-авторизации в личном кабинете студента.
 *
 * Реализует поток Authorization Code + PKCE:
 * создание запроса авторизации и обмен кода на токен доступа.
 */
interface JournalAuthRepository {

    /**
     * Создает запрос авторизации для открытия в WebView.
     *
     * Генерирует одноразовые state и code_verifier (PKCE) и возвращает
     * URL страницы авторизации. code_verifier сохраняется до вызова [exchangeCode].
     *
     * @param silent Если `true`, добавляет `prompt=none` — провайдер вернет
     * код без показа UI при живой SSO-сессии или ошибку `interaction_required`.
     * @return Пара (URL авторизации, state).
     */
    fun createAuthRequest(silent: Boolean): JournalAuthRequest

    /**
     * Обменивает код авторизации на токен доступа.
     *
     * @param code Код авторизации из callback URL.
     * @param state Значение state из callback URL (для поиска code_verifier).
     * @return Токен доступа [JournalAuthToken].
     * @throws StudentAuthorizedException Если state неизвестен или обмен завершился ошибкой.
     */
    suspend fun exchangeCode(code: String, state: String): JournalAuthToken
}
