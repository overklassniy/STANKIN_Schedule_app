package com.overklassniy.stankinschedule.journal.login.ui

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.overklassniy.stankinschedule.journal.core.domain.model.JournalAuthRequest
import com.overklassniy.stankinschedule.journal.core.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel экрана входа в журнал.
 *
 * Управляет OIDC-авторизацией через WebView: бесшовная попытка входа
 * при живой SSO-сессии (prompt=none), интерактивный вход и обмен кода на токен.
 */
@HiltViewModel
class JournalLoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    /**
     * Текущий OIDC-запрос, который необходимо открыть в WebView.
     * `null` — WebView не требуется.
     */
    private val _authRequest = MutableStateFlow<JournalAuthRequest?>(null)
    val authRequest = _authRequest.asStateFlow()

    /**
     * Показывать ли WebView пользователю (интерактивный вход).
     * `false` при скрытой бесшовной попытке.
     */
    private val _webAuthVisible = MutableStateFlow(false)
    val webAuthVisible = _webAuthVisible.asStateFlow()

    private val _loginState = MutableStateFlow(false)
    val loginState = _loginState.asStateFlow()

    private val _isLogging = MutableStateFlow(false)
    val isLogging = _isLogging.asStateFlow()

    private val _loginError = MutableStateFlow<Throwable?>(null)
    val loginError = _loginError.asStateFlow()

    /**
     * Завершилась ли бесшовная попытка входа.
     * Пока `false` и попытка активна, на экране показывается прогресс.
     */
    private val _silentFinished = MutableStateFlow(false)
    val silentFinished = _silentFinished.asStateFlow()

    init {
        _authRequest.value = loginUseCase.beginSilentSignIn()
    }

    /**
     * Запускает интерактивный вход: открывает страницу авторизации в WebView.
     */
    fun beginSignIn() {
        _loginError.value = null
        _authRequest.value = loginUseCase.beginSignIn()
        _webAuthVisible.value = true
    }

    /**
     * Отменяет текущий запрос авторизации и закрывает WebView.
     */
    fun cancelWebAuth() {
        _authRequest.value = null
        _webAuthVisible.value = false
        _silentFinished.value = true
    }

    /**
     * Обрабатывает перехваченный WebView callback-редирект на страницу личного кабинета.
     *
     * При наличии code+state запускает обмен на токен;
     * при ошибке завершает бесшовную попытку и показывает экран входа.
     *
     * @param url Полный URL callback-редиректа.
     */
    fun onAuthCallback(url: String) {
        val request = _authRequest.value ?: return

        val uri = url.toUri()
        val code = uri.getQueryParameter("code")
        val state = uri.getQueryParameter("state")
        val error = uri.getQueryParameter("error")
        val isSilent = !_webAuthVisible.value

        _authRequest.value = null
        _webAuthVisible.value = false

        if (code != null && state != null && state == request.state) {
            exchangeCode(code, state)
            return
        }

        _silentFinished.value = true
        if (!isSilent || (error != null && error != ERROR_INTERACTION_REQUIRED)) {
            _loginError.value = StudentAuthorizedException(error ?: "Authorization failed")
        }
    }

    /**
     * Обрабатывает ошибку загрузки страницы авторизации в WebView.
     */
    fun onAuthError() {
        val isSilent = !_webAuthVisible.value
        cancelWebAuth()

        if (!isSilent) {
            _loginError.value = StudentAuthorizedException("Authorization page load failed")
        }
    }

    /**
     * Обменивает код авторизации на токен и завершает вход.
     */
    private fun exchangeCode(code: String, state: String) {
        viewModelScope.launch {
            _loginError.value = null
            _isLogging.value = true

            loginUseCase.completeSignIn(code, state)
                .catch { e -> _loginError.value = e }
                .collect {
                    _loginState.value = true
                }

            _isLogging.value = false
        }
    }

    private companion object {
        /** Ошибка OIDC, означающая отсутствие живой SSO-сессии. */
        private const val ERROR_INTERACTION_REQUIRED = "interaction_required"
    }
}
