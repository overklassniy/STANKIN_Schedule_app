package com.overklassniy.stankinschedule.settings.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.core.ui.components.UIState
import com.overklassniy.stankinschedule.core.ui.ext.setVisibility
import com.overklassniy.stankinschedule.core.ui.utils.BrowserUtils
import com.overklassniy.stankinschedule.core.ui.utils.exceptionDescription
import com.overklassniy.stankinschedule.settings.ui.databinding.ActivityMarkdownViewerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MarkdownViewerActivity : AppCompatActivity() {

    @Inject
    lateinit var loggerAnalytics: LoggerAnalytics

    private val viewModel: MarkdownViewerViewModel by viewModels()

    private lateinit var binding: ActivityMarkdownViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMarkdownViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""
        val url = intent.getStringExtra(EXTRA_URL) ?: ""

        binding.toolbar.title = title
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.markdownRefresh.setOnRefreshListener { 
            viewModel.loadMarkdown(url, force = true) 
        }
        binding.errorAction.setOnClickListener { 
            viewModel.loadMarkdown(url) 
        }

        setupWebViewSettings()
        viewModel.loadMarkdown(url)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.markdownContent.collect { state ->
                    when (state) {
                        is UIState.Success -> {
                            updateContent(state.data)
                        }
                        is UIState.Failed -> {
                            val description = exceptionDescription(state.error)
                            binding.errorTitle.text = description ?: state.error.toString()
                        }
                        is UIState.Loading -> {
                            // Loading state
                        }
                    }
                    updateVisibleView(state)
                }
            }
        }

        loggerAnalytics.logEvent(LoggerAnalytics.SCREEN_ENTER, "MarkdownViewerActivity")
    }

    override fun onDestroy() {
        super.onDestroy()
        loggerAnalytics.logEvent(LoggerAnalytics.SCREEN_LEAVE, "MarkdownViewerActivity")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebViewSettings() {
        binding.markdownView.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                supportDarkMode(isDarkTheme = isDarkMode())
            }

            settings.apply {
                allowFileAccess = true
                loadsImagesAutomatically = true
                javaScriptEnabled = true
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    url: String?
                ): Boolean {
                    url?.let { BrowserUtils.openLink(this@MarkdownViewerActivity, it) }
                    return true
                }
            }
        }
    }

    private fun isDarkMode(): Boolean {
        return when (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun updateContent(markdown: String) {
        val html = convertMarkdownToHtml(markdown, isDarkMode())
        binding.markdownView.loadDataWithBaseURL(
            null,
            html,
            "text/html; charset=UTF-8",
            "UTF-8",
            null
        )
    }

    private fun convertMarkdownToHtml(markdown: String, isDark: Boolean): String {
        val backgroundColor = if (isDark) "#121212" else "#FFFFFF"
        val textColor = if (isDark) "#E0E0E0" else "#000000"
        val linkColor = if (isDark) "#90CAF9" else "#1976D2"
        
        // Разбить на строки для обработки
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
                    htmlLines.add("<br>")
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
                    line-height: 1.6;
                    max-width: 800px;
                    margin: 0 auto;
                }
                h1, h2, h3, h4 {
                    color: $textColor;
                    margin-top: 24px;
                    margin-bottom: 16px;
                }
                h1 { font-size: 2em; }
                h2 { font-size: 1.5em; }
                h3 { font-size: 1.25em; }
                h4 { font-size: 1.1em; }
                p { margin: 16px 0; }
                ul, ol { margin: 16px 0; padding-left: 24px; }
                li { margin: 8px 0; }
                a { color: $linkColor; text-decoration: none; }
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

    private fun processInlineMarkdown(text: String, linkColor: String): String {
        return text
            .replace(Regex("\\*\\*(.+?)\\*\\*"), "<strong>$1</strong>")
            .replace(Regex("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)"), "<em>$1</em>")
            .replace(Regex("\\[([^\\]]+)\\]\\(([^\\)]+)\\)"), "<a href=\"$2\" style=\"color: $linkColor;\">$1</a>")
    }

    private fun updateVisibleView(state: UIState<*>) {
        binding.markdownView.setVisibility(state is UIState.Success)
        binding.markdownRefresh.isRefreshing = state is UIState.Loading
        binding.markdownError.setVisibility(state is UIState.Failed)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun WebView.supportDarkMode(isDarkTheme: Boolean) {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            val forceDarkMode = if (isDarkTheme) {
                WebSettingsCompat.FORCE_DARK_ON
            } else {
                WebSettingsCompat.FORCE_DARK_OFF
            }
            WebSettingsCompat.setForceDark(settings, forceDarkMode)
        }
    }

    companion object {
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_URL = "extra_url"

        fun createIntent(context: Context, title: String, url: String): Intent {
            return Intent(context, MarkdownViewerActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_URL, url)
            }
        }
    }
}
