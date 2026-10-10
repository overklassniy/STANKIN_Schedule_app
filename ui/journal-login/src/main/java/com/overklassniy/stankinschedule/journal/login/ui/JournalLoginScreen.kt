package com.overklassniy.stankinschedule.journal.login.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.components.TrackCurrentScreen
import com.overklassniy.stankinschedule.core.ui.ext.Zero
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.core.ui.utils.BrowserUtils
import com.overklassniy.stankinschedule.journal.core.domain.utils.Constants
import com.overklassniy.stankinschedule.journal.login.ui.components.AuthWebView
import com.overklassniy.stankinschedule.journal.login.ui.components.LoginError
import com.overklassniy.stankinschedule.journal.login.ui.components.LoginToolBar


/**
 * Экран входа в модуль «Журнал».
 *
 * Авторизация проходит через OIDC в WebView: сначала выполняется
 * бесшовная попытка входа (при живой SSO-сессии), затем — при необходимости —
 * интерактивный вход на странице личного кабинета.
 *
 * @param viewModel ViewModel экрана.
 * @param navigateToJournal Навигация в модуль журнала после успешного входа.
 * @param modifier Модификатор.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalLoginScreen(
    viewModel: JournalLoginViewModel,
    navigateToJournal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TrackCurrentScreen(screen = "JournalLoginScreen")

    val context = LocalContext.current

    val loginState by viewModel.loginState.collectAsState()
    LaunchedEffect(loginState) {
        if (loginState) {
            navigateToJournal()
        }
    }

    val authRequest by viewModel.authRequest.collectAsState()
    val webAuthVisible by viewModel.webAuthVisible.collectAsState()
    val isLogging by viewModel.isLogging.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    val silentFinished by viewModel.silentFinished.collectAsState()

    // Кнопка "Назад" во время интерактивного входа закрывает WebView
    BackHandler(enabled = webAuthVisible) {
        viewModel.cancelWebAuth()
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            LoginToolBar(
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.Zero,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            val request = authRequest
            when {
                // Интерактивный вход: страница авторизации на весь экран
                request != null && webAuthVisible -> {
                    AuthWebView(
                        url = request.url,
                        onCallback = viewModel::onAuthCallback,
                        onError = viewModel::onAuthError,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Бесшовная попытка входа или обмен кода на токен
                request != null || isLogging || !silentFinished -> {
                    // Скрытый WebView для бесшовной попытки (prompt=none)
                    if (request != null) {
                        AuthWebView(
                            url = request.url,
                            onCallback = viewModel::onAuthCallback,
                            onError = viewModel::onAuthError,
                            modifier = Modifier.size(1.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(R.string.journal_login_progress),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = Dimen.ContentPadding)
                        )
                    }
                }

                // Экран входа: кнопка авторизации через личный кабинет
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(Dimen.ContentPadding)
                    ) {

                        loginError?.let { error ->
                            LoginError(
                                error = error,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = Dimen.ContentPadding)
                            )
                        }

                        Text(
                            text = stringResource(R.string.journal_login_hint),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimen.ContentPadding)
                        )

                        Button(
                            onClick = { viewModel.beginSignIn() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.journal_sign_in_lk))
                        }

                        TextButton(
                            onClick = { BrowserUtils.openLink(context, Constants.LKO_WEB_URL) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.journal_open_lk))
                        }
                    }
                }
            }

            if (isLogging) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}
