package com.overklassniy.stankinschedule.journal.login.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.overklassniy.stankinschedule.journal.core.domain.utils.Constants

/**
 * WebView для OIDC-авторизации в личном кабинете.
 *
 * Загружает страницу авторизации trusted.stankin.ru и перехватывает
 * редирект на callback-адрес личного кабинета, передавая URL наружу
 * (страница callback физически не загружается).
 *
 * @param url URL страницы авторизации со всеми OIDC-параметрами.
 * @param onCallback Вызывается при перехвате callback-редиректа с полным URL.
 * @param onError Вызывается при ошибке загрузки основного фрейма.
 * @param modifier Модификатор.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AuthWebView(
    url: String,
    onCallback: (url: String) -> Unit,
    onError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webViewClient = object : WebViewClient() {

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean {
                        return handleUrl(request.url.toString())
                    }

                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        return handleUrl(url)
                    }

                    override fun onReceivedError(
                        view: WebView,
                        request: WebResourceRequest,
                        error: WebResourceError,
                    ) {
                        if (request.isForMainFrame) {
                            onError()
                        }
                    }

                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        // Подстраховка: некоторые версии WebView не вызывают
                        // shouldOverrideUrlLoading для редиректов
                        if (url.startsWith(Constants.REDIRECT_URI)) {
                            view.stopLoading()
                            onCallback(url)
                        }
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        // Виджет авторизации задаёт корневому контейнеру фиксированную
                        // высоту — растягиваем её на весь экран
                        view.evaluateJavascript(INJECT_FILL_HEIGHT_JS, null)
                    }

                    private fun handleUrl(url: String): Boolean {
                        return if (url.startsWith(Constants.REDIRECT_URI)) {
                            onCallback(url)
                            true
                        } else {
                            false
                        }
                    }
                }
            }
        },
        update = { webView ->
            // url страницы меняется в ходе OIDC-редиректов, поэтому сравнивать
            // текущий url нельзя — запоминаем загруженный запрос в tag
            if (webView.tag != url) {
                webView.tag = url
                webView.loadUrl(url)
            }
        },
        modifier = modifier
    )
}

/**
 * Скрипт, растягивающий корневой контейнер виджета авторизации
 * на всю высоту экрана (исходно контейнер ограничен содержимым).
 *
 * Высота задаётся в пикселях через `window.innerHeight`: в WebView
 * единицы vh и проценты от корня вычисляются некорректно.
 * Тег style сохраняет правило при перерендере виджета на клиенте,
 * слушатель resize обновляет высоту при повороте экрана.
 */
private const val INJECT_FILL_HEIGHT_JS = """
    (function() {
        function applyFill() {
            var style = document.getElementById('app-auth-fill-style');
            if (!style) {
                style = document.createElement('style');
                style.id = 'app-auth-fill-style';
                (document.head || document.documentElement).appendChild(style);
            }
            style.textContent = 'body > div { min-height: ' + window.innerHeight + 'px !important; }';
        }
        applyFill();
        if (!window.__appAuthFillBound) {
            window.__appAuthFillBound = true;
            window.addEventListener('resize', applyFill);
        }
    })();
"""
