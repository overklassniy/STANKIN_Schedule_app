package com.overklassniy.stankinschedule.news.viewer.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewFeature
import coil.load
import com.overklassniy.stankinschedule.core.domain.ext.formatDate
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.core.ui.components.UIState
import com.overklassniy.stankinschedule.core.ui.ext.setVisibility
import com.overklassniy.stankinschedule.core.ui.utils.BrowserUtils
import com.overklassniy.stankinschedule.core.ui.utils.exceptionDescription
import com.overklassniy.stankinschedule.core.ui.utils.newsImageLoader
import com.overklassniy.stankinschedule.news.core.domain.model.NewsContent
import com.overklassniy.stankinschedule.news.viewer.ui.databinding.ActivityNewsViewerBinding
import com.overklassniy.stankinschedule.news.viewer.ui.utils.NewsBrowserUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
/**
 * Экран просмотра содержимого новости.
 *
 * Отвечает за:
 * - чтение аргументов из Intent;
 * - настройку WebView и загрузку HTML через Quill;
 * - обработку состояний загрузки/ошибки;
 * - действия тулбара (открыть в браузере, поделиться, обновить).
 */
class NewsViewerActivity : AppCompatActivity() {

    @Inject
    lateinit var loggerAnalytics: LoggerAnalytics

    private val viewModel: NewsViewerViewModel by viewModels()

    private lateinit var binding: ActivityNewsViewerBinding

    private val imageLoader by lazy { newsImageLoader(this) }
    private var newsId: Int = -1

    /**
     * Инициализирует экран просмотра новости.
     *
     * Алгоритм:
     * 1. Инициализирует binding и тулбар.
     * 2. Читает аргументы из intent.
     * 3. Настраивает WebView и подписки на состояние `newsContent`.
     * 4. Запускает первичную загрузку данных.
     *
     * @param savedInstanceState Сохраненное состояние activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Аргументы
        val newsTitle = intent.getStringExtra(NEWS_TITLE)
        newsId = intent.getIntExtra(NEWS_ID, -1)

        binding.toolbar.title = newsTitle
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener(this::onMenuItemClickListener)

        binding.newsRefresh.setOnRefreshListener { viewModel.loadNewsContent(newsId, force = true) }
        binding.errorAction.setOnClickListener { viewModel.loadNewsContent(newsId) }

        setupWebViewSettings()
        viewModel.loadNewsContent(newsId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newsContent.collect { state ->
                    if (state is UIState.Success) {
                        updateContent(state.data)
                    }
                    if (state is UIState.Failed) {
                        binding.errorTitle.text =
                            this@NewsViewerActivity.exceptionDescription(state.error)
                    }

                    updateVisibleView(state)
                }
            }
        }

        loggerAnalytics.logEvent(LoggerAnalytics.SCREEN_ENTER, "NewsViewerActivity")
    }

    /**
     * Логирует уход со страницы.
     */
    override fun onDestroy() {
        super.onDestroy()
        loggerAnalytics.logEvent(LoggerAnalytics.SCREEN_LEAVE, "NewsViewerActivity")
    }

    /**
     * Обработчик кликов по пунктам меню тулбара.
     *
     * @param item Пункт меню.
     * @return true если событие обработано, иначе делегируется базовой реализации.
     */
    private fun onMenuItemClickListener(item: MenuItem): Boolean {
        when (item.itemId) {
            // Открыть в браузере
            R.id.open_in_browser -> {
                openLink(NewsBrowserUtils.linkForPost(newsId))
                return true
            }
            // Поделится
            R.id.news_share -> {
                val url = NewsBrowserUtils.linkForPost(newsId)
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, url)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                return true
            }
            // Обновить
            R.id.news_update -> {
                viewModel.loadNewsContent(newsId, force = true)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetJavaScriptEnabled")
    /**
     * Настраивает WebView для отображения контента новости.
     *
     * Алгоритм:
     * 1. Инициализирует WebViewAssetLoader для доступа к ресурсам в assets.
     * 2. Включает JavaScript и загрузку изображений.
     * 3. Регистрирует JS-интерфейс [NewsViewInterface] под именем "Android".
     * 4. Настраивает перехват ссылок и ресурсов.
     *
     * Примечания:
     * - JavaScript включен, так как Quill требует JS для рендера.
     */
    private fun setupWebViewSettings() {
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        binding.newsView.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                supportDarkMode(isDarkTheme = isDarkMode())
            }

            settings.apply {
                allowFileAccess = true
                loadsImagesAutomatically = true
                javaScriptEnabled = true
            }

            addJavascriptInterface(NewsViewInterface {
            }, "Android")
        }

        // Переадресация ссылок
        binding.newsView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                openLink(request?.url.toString())
                return true
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest,
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }
    }

    /**
     * Открывает ссылку во внешнем браузере/приложении.
     *
     * @param url Абсолютный URL.
     */
    private fun openLink(url: String) {
        BrowserUtils.openLink(this, url)
    }

    /**
     * Возвращает цвет фона экрана новостей в HEX-формате.
     *
     * @return Строка HEX вида #RRGGBB.
     */
    private fun newsBackgroundHex(): String {
        val backgroundColor = resources.getColor(R.color.news_viewer_background, theme)
        return "#" + Integer.toHexString(backgroundColor).drop(2)
    }

    /**
     * Обновляет UI контентом новости.
     *
     * @param content Модель полной новости.
     */
    private fun updateContent(content: NewsContent) {
        binding.newsPreview.load(content.previewImageUrl, imageLoader)
        binding.toolbar.title = content.title
        binding.newsDate.text = formatDate(content.date)

        binding.newsView.loadDataWithBaseURL(
            null,
            content.prepareQuillPage(newsBackgroundHex()),
            "text/html; charset=UTF-8",
            "UTF-8",
            null
        )
    }

    /**
     * Обновляет видимость элементов в зависимости от состояния загрузки.
     *
     * @param state Текущее состояние UI.
     */
    private fun updateVisibleView(state: UIState<*>) {
        binding.newsView.setVisibility(state is UIState.Success)
        binding.newsRefresh.isRefreshing = state is UIState.Loading
        binding.newsError.setVisibility(state is UIState.Failed)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    /**
     * Включает алгоритмическое затемнение (ALG DARKENING) в WebView, если поддерживается.
     *
     * @param isDarkTheme Признак темной темы.
     */
    private fun WebView.supportDarkMode(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, true)
            }
        }
    }

    /**
     * Определяет, включен ли режим темной темы.
     *
     * @return true, если активна темная тема.
     */
    private fun isDarkMode(): Boolean {
        val currentNightMode = resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * JS-интерфейс для обратного вызова из страницы после рендера контента.
     *
     * @param loaded Колбэк, вызываемый после завершения загрузки контента.
     */
    class NewsViewInterface(private val loaded: () -> Unit) {
        /**
         * Обратный вызов из JS после рендера Quill.
         *
         * Примечания:
         * - Вызывается скриптом страницы через Android.onNewsLoaded().
         */
        @JavascriptInterface
        @Suppress("Unused")
        fun onNewsLoaded() {
            loaded.invoke()
        }
    }

    companion object {
        private const val NEWS_TITLE = "news_title"
        private const val NEWS_ID = "news_id"
    }
}