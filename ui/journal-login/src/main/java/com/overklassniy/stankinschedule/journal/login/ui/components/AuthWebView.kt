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
