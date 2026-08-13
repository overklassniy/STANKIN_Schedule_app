package com.overklassniy.stankinschedule.settings.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.overklassniy.stankinschedule.core.ui.components.AppScaffold
import com.overklassniy.stankinschedule.core.ui.components.BackButton
import com.overklassniy.stankinschedule.core.ui.components.UIState
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.core.ui.utils.BrowserUtils
import com.overklassniy.stankinschedule.core.ui.utils.exceptionDescription

/**
 * Экран просмотра Markdown-документов (Privacy Policy, Terms и т.д.).
 *
 * Отображает содержимое в WebView с поддержкой тёмной темы,
 * обработкой состояний загрузки и ошибки.
 *
 * @param title Заголовок экрана.
 * @param state Текущее состояние загрузки.
 * @param onRetry Обработчик повторной попытки при ошибке.
 * @param onBackPressed Действие «назад».
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownViewerScreen(
    title: String,
    state: UIState<String>,
    onRetry: () -> Unit,
    onBackPressed: () -> Unit,
) {
    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = { BackButton(onClick = onBackPressed) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state) {
                is UIState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UIState.Success -> {
                    MarkdownWebView(
                        markdown = state.data,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is UIState.Failed -> {
                    ErrorView(
                        errorDescription = exceptionDescription(state.error),
                        onRetry = onRetry,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * WebView для отображения Markdown-контента, преобразованного в HTML.
 *
 * @param markdown Исходный Markdown-текст.
 * @param modifier Модификатор.
 */
@Composable
private fun MarkdownWebView(
    markdown: String,
    modifier: Modifier = Modifier,
) {
    val isDark = isDarkTheme()

    val html = remember(markdown, isDark) {
        convertMarkdownToHtml(markdown, isDark)
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                setupWebView(this, isDark, ctx)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                null,
                html,
                "text/html; charset=UTF-8",
                "UTF-8",
                null
            )
        },
        modifier = modifier
    )
}

/**
 * Экран ошибки с кнопкой повторной попытки.
 *
 * @param errorDescription Текст ошибки.
 * @param onRetry Обработчик повторной попытки.
 * @param modifier Модификатор.
 */
@Composable
private fun ErrorView(
    errorDescription: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(Dimen.ContentPadding)
    ) {
        Text(
            text = errorDescription,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.ContentPadding)
        )
        OutlinedButton(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

/**
 * Настраивает WebView для отображения Markdown и перехвата ссылок.
 *
 * @param webView Экземпляр WebView.
 * @param isDark Признак тёмной темы.
 * @param context Контекст.
 */
@SuppressLint("SetJavaScriptEnabled")
private fun setupWebView(webView: WebView, isDark: Boolean, context: android.content.Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        if (isDark && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            @Suppress("DEPRECATION")
            WebSettingsCompat.setForceDark(webView.settings, WebSettingsCompat.FORCE_DARK_ON)
        } else if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            @Suppress("DEPRECATION")
            WebSettingsCompat.setForceDark(webView.settings, WebSettingsCompat.FORCE_DARK_OFF)
        }
    }

    webView.settings.apply {
        allowFileAccess = true
        loadsImagesAutomatically = true
        javaScriptEnabled = true
    }

    webView.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest,
        ): Boolean {
            BrowserUtils.openLink(context, request.url.toString())
            return true
        }
    }
}

/**
 * Определяет, включена ли тёмная тема.
 */
@Composable
private fun isDarkTheme(): Boolean {
    val configuration = LocalConfiguration.current
    val currentNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return currentNightMode == Configuration.UI_MODE_NIGHT_YES
}

/**
 * Преобразует Markdown в HTML с поддержкой тёмной темы.
 *
 * @param markdown Исходный Markdown-текст.
 * @param isDark Признак тёмной темы.
 * @return HTML-страница.
 */
private fun convertMarkdownToHtml(markdown: String, isDark: Boolean): String {
    val backgroundColor = if (isDark) "#121212" else "#FFFFFF"
    val textColor = if (isDark) "#E0E0E0" else "#000000"
    val linkColor = if (isDark) "#90CAF9" else "#1976D2"

    val lines = markdown.lines()
    val htmlLines = mutableListOf<String>()
    var inList = false
    var listType = ""

    for (line in lines) {
        val trimmed = line.trim()

        when {
            trimmed.startsWith("# ") -> {
                if (inList) {
                    htmlLines.add(if (listType == "ul") "</ul>" else "</ol>")
                    inList = false
                }
                htmlLines.add("<h1>${trimmed.substring(2)}</h1>")
            }

            trimmed.startsWith("## ") -> {
                if (inList) {
                    htmlLines.add(if (listType == "ul") "</ul>" else "</ol>")
                    inList = false
                }
                htmlLines.add("<h2>${trimmed.substring(3)}</h2>")
            }

            trimmed.startsWith("### ") -> {
                if (inList) {
                    htmlLines.add(if (listType == "ul") "</ul>" else "</ol>")
                    inList = false
                }
                htmlLines.add("<h3>${trimmed.substring(4)}</h3>")
            }

            trimmed.startsWith("#### ") -> {
                if (inList) {
                    htmlLines.add(if (listType == "ul") "</ul>" else "</ol>")
                    inList = false
                }
                htmlLines.add("<h4>${trimmed.substring(5)}</h4>")
            }

            trimmed.startsWith("- ") || trimmed.matches(Regex("^\\d+\\. .+")) -> {
                val currentListType = if (trimmed.startsWith("- ")) "ul" else "ol"
                if (!inList || listType != currentListType) {
                    if (inList) {
                        htmlLines.add(if (listType == "ul") "</ul>" else "</ol>")
                    }
                    htmlLines.add(if (currentListType == "ul") "<ul>" else "<ol>")
                    inList = true
                    listType = currentListType
                }
                val content = if (trimmed.startsWith("- ")) {
                    trimmed.substring(2)
                } else {
                    trimmed.replaceFirst(Regex("^\\d+\\. "), "")
                }
                htmlLines.add("<li>${processInlineMarkdown(content, linkColor)}</li>")
            }

            trimmed.isEmpty() -> {
                if (inList) {
                    htmlLines.add(if (listType == "ul") "</ul>" else "</ol>")
                    inList = false
                }
            }

            else -> {
                if (inList) {
                    htmlLines.add(if (listType == "ul") "</ul>" else "</ol>")
                    inList = false
                }
                htmlLines.add("<p>${processInlineMarkdown(trimmed, linkColor)}</p>")
            }
        }
    }

    if (inList) {
        htmlLines.add(if (listType == "ul") "</ul>" else "</ol>")
    }

    val html = htmlLines.joinToString("\n")

    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                background-color: $backgroundColor;
                color: $textColor;
                padding: 16px;
                line-height: 1.5;
                max-width: 800px;
                margin: 0 auto;
                overflow-wrap: break-word;
                word-break: break-word;
            }
            h1, h2, h3, h4 {
                color: $textColor;
                margin-top: 12px;
                margin-bottom: 6px;
            }
            h1 { font-size: 1.5em; }
            h2 { font-size: 1.25em; }
            h3 { font-size: 1.1em; }
            h4 { font-size: 1em; }
            p { margin: 6px 0; }
            ul, ol { margin: 6px 0; padding-left: 20px; }
            li { margin: 2px 0; }
            a { color: $linkColor; text-decoration: none; word-break: break-all; }
            a:hover { text-decoration: underline; }
            strong { font-weight: 600; }
            em { font-style: italic; }
        </style>
    </head>
    <body>
        $html
    </body>
    </html>
    """.trimIndent()
}

/**
 * Обрабатывает инлайн-форматирование Markdown (жирный, курсив, ссылки).
 */
private fun processInlineMarkdown(text: String, linkColor: String): String {
    return text
        .replace(Regex("\\*\\*(.+?)\\*\\*"), "<strong>$1</strong>")
        .replace(Regex("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)"), "<em>$1</em>")
        .replace(
            Regex("\\[([^]]+)]\\(([^)]+)\\)"),
            "<a href=\"$2\" style=\"color: $linkColor;\">$1</a>"
        )
}
