package com.overklassniy.stankinschedule.journal.core.data.repository

import androidx.core.net.toUri
import com.overklassniy.stankinschedule.journal.core.data.api.ModuleJournalAPI
import com.overklassniy.stankinschedule.journal.core.data.utils.PkceGenerator
import com.overklassniy.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.overklassniy.stankinschedule.journal.core.domain.model.JournalAuthRequest
import com.overklassniy.stankinschedule.journal.core.domain.model.JournalAuthToken
import com.overklassniy.stankinschedule.journal.core.domain.repository.JournalAuthRepository
import com.overklassniy.stankinschedule.journal.core.domain.utils.Constants
import retrofit2.await
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * Реализация репозитория OIDC-авторизации [JournalAuthRepository].
 *
 * Формирует URL авторизации trusted.stankin.ru и обменивает
 * код авторизации на токен через API личного кабинета.
 *
 * @param api Интерфейс API для выполнения запросов.
 */
class JournalAuthRepositoryImpl @Inject constructor(
    private val api: ModuleJournalAPI,
) : JournalAuthRepository {

    // Одноразовые code_verifier, ожидающие обмена: state -> verifier
    private val pendingVerifiers = ConcurrentHashMap<String, String>()

    /**
     * Создает запрос авторизации Authorization Code + PKCE.
     *
     * @param silent `true` — добавляет prompt=none (бесшовный вход при живой SSO-сессии);
     * `false` — добавляет prompt=consent (интерактивный вход с формой логина).
     * @return Объект [JournalAuthRequest] с URL и state.
     */
    override fun createAuthRequest(silent: Boolean): JournalAuthRequest {
        val state = PkceGenerator.generateState()
        val verifier = PkceGenerator.generateVerifier()
        pendingVerifiers[state] = verifier

        val url = Constants.AUTH_URL.toUri()
            .buildUpon()
            .appendPath("api")
            .appendPath("oidc")
            .appendPath("auth")
            .appendQueryParameter("client_id", Constants.CLIENT_ID)
            .appendQueryParameter("redirect_uri", Constants.REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", Constants.AUTH_SCOPE)
            .appendQueryParameter("code_challenge", PkceGenerator.challenge(verifier))
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("prompt", if (silent) "none" else "consent")
            .appendQueryParameter("state", state)
            .build()
            .toString()

        return JournalAuthRequest(url = url, state = state)
    }

    /**
     * Обменивает код авторизации на токен доступа.
     *
     * @param code Код авторизации из callback URL.
     * @param state State из callback URL для поиска сохраненного code_verifier.
     * @return Токен доступа [JournalAuthToken].
     * @throws StudentAuthorizedException Если state неизвестен или обмен завершился ошибкой.
     */
    override suspend fun exchangeCode(code: String, state: String): JournalAuthToken {
        val verifier = pendingVerifiers.remove(state)
            ?: throw StudentAuthorizedException("Unknown auth state")

        return try {
            val response = api.getToken(
                code = code,
                clientId = Constants.CLIENT_ID,
                codeVerifier = verifier,
            ).await()

            JournalAuthToken(
                accessToken = response.accessToken,
                obtainedAtMillis = System.currentTimeMillis(),
            )
        } catch (e: Exception) {
            throw StudentAuthorizedException(e)
        }
    }
}
