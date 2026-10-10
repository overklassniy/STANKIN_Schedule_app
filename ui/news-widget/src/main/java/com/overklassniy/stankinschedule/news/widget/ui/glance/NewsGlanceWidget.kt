package com.overklassniy.stankinschedule.news.widget.ui.glance

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.NoRippleOverride
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.currentState
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.overklassniy.stankinschedule.core.domain.settings.ThemeColors
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision
import com.overklassniy.stankinschedule.news.widget.domain.model.NewsWidgetData
import com.overklassniy.stankinschedule.news.widget.ui.R
import com.overklassniy.stankinschedule.news.widget.ui.configure.NewsWidgetConfigureActivity
import dagger.hilt.android.EntryPointAccessors

/**
 * Glance-виджет новостей.
 *
 * Использует [PreferencesGlanceStateDefinition] для хранения конфигурации каждого
 * экземпляра виджета. Состояние сериализуется через [NewsWidgetStateKeys].
 *
 * Отображает объединённый список из 10 последних новостей из выбранных категорий
 * с цветными метками категорий. Клик по новости открывает URL во внешнем браузере.
 */
class NewsGlanceWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Single

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            Log.d(TAG, "provideGlance: start, id=$id")

            val entryPoint = EntryPointAccessors.fromApplication(
                context,
                NewsGlanceEntryPoint::class.java
            )
            val useCase = entryPoint.newsWidgetUseCase()

            val manager = GlanceAppWidgetManager(context)
            val appWidgetId = manager.getAppWidgetId(id)

            // Записываем isDark в state, чтобы при смене темы MainApplication
            // мог обновить этот ключ и триггерить рекомпозицию через currentState
            val initialIsDark = isSystemDarkTheme(context)
            updateAppWidgetState(context, id) { prefs ->
                prefs[NewsWidgetStateKeys.isDarkTheme] = initialIsDark
            }

            provideContent {
                Log.d(TAG, "provideContent: composing, appWidgetId=$appWidgetId")
                val currentPrefs = currentState<Preferences>()
                val data = NewsWidgetStateKeys.toData(currentPrefs)
                // isDark из state, fallback на initialIsDark — currentState может
                // читать snapshot до записи updateAppWidgetState при первом рендере
                val isDark = currentPrefs[NewsWidgetStateKeys.isDarkTheme] ?: initialIsDark

                var news by remember(data?.hashCode()) {
                    mutableStateOf<List<Pair<NewsSubdivision, NewsPost>>>(emptyList())
                }

                LaunchedEffect(data?.hashCode()) {
                    if (data != null) {
                        Log.d(TAG, "LaunchedEffect: loading news")
                        news = useCase.lastNews(data.selectedSubdivisions, 10)
                        Log.d(TAG, "LaunchedEffect: news loaded, size=${news.size}")
                    }
                }

                WidgetContent(LocalContext.current, appWidgetId, data, news, isDark)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "provideGlance: FATAL ERROR", e)
            try {
                EntryPointAccessors.fromApplication(context, NewsGlanceEntryPoint::class.java)
                    .crashReporter()
                    .recordException(e)
            } catch (reportError: Throwable) {
                Log.e(TAG, "provideGlance: failed to report error", reportError)
            }
            throw e
        }
    }

    /**
     * Содержимое виджета: заголовок и список новостей.
     *
     * @param context Контекст приложения.
     * @param appWidgetId Идентификатор виджета.
     * @param data Данные конфигурации виджета или null.
     * @param news Список пар: категория и новость.
     * @param isDark Признак тёмной системной темы.
     */
    @androidx.compose.runtime.Composable
    private fun WidgetContent(
        context: Context,
        appWidgetId: Int,
        data: NewsWidgetData?,
        news: List<Pair<NewsSubdivision, NewsPost>>,
        isDark: Boolean,
    ) {
        if (data == null) {
            EmptyState(context)
            return
        }

        val themeColors = data.themeColors(isDark)

        val backgroundColor = Color(hexToArgb(themeColors.background))
        val primaryContainerColor = Color(hexToArgb(themeColors.primaryContainer))
        val onPrimaryContainerColor = Color(hexToArgb(themeColors.onPrimaryContainer))
        val onBackgroundColor = Color(hexToArgb(themeColors.onBackground))

        // Структура виджета намеренно плоская (без вложенных Row):
        // на части устройств (Transsion XOS, Android 10) вложенные контейнеры
        // RemoteViews рендерятся с перепутанным содержимым.
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(backgroundColor)
                .cornerRadius(16.dp)
        ) {
            Header(
                context = context,
                appWidgetId = appWidgetId,
                primaryContainerColor = primaryContainerColor,
                onPrimaryContainerColor = onPrimaryContainerColor,
            )

            if (news.isEmpty()) {
                Text(
                    text = context.getString(R.string.news_widget_no_news),
                    style = TextStyle(
                        color = fixedColorProvider(onBackgroundColor),
                        fontSize = 14.sp,
                    ),
                    modifier = GlanceModifier.padding(16.dp),
                )
            } else {
                NewsList(
                    context = context,
                    news = news,
                    themeColors = themeColors,
                    onBackgroundColor = onBackgroundColor,
                    announcementsColor = data.announcementsColor,
                )
            }
        }
    }

    /**
     * Заголовок виджета. Нажатие открывает экран настройки виджета.
     *
     * Заголовок представлен одним [Text], а не Row с иконкой: вложенные Row
     * с несколькими дочерними элементами некорректно рендерятся на части устройств
     * (например, Transsion XOS на Android 10).
     *
     * @param context Контекст приложения.
     * @param appWidgetId Идентификатор виджета.
     * @param primaryContainerColor Цвет фона заголовка.
     * @param onPrimaryContainerColor Цвет текста заголовка.
     */
    @androidx.compose.runtime.Composable
    private fun Header(
        context: Context,
        appWidgetId: Int,
        primaryContainerColor: Color,
        onPrimaryContainerColor: Color,
    ) {
        // На Transsion pre-S Glance оборачивает clickable-элементы в Box с ripple-
        // изображением. Для заголовка внутри корневого Column эта Box-обёртка
        // вызывает перекрытие с контентом ниже. Поэтому на сломанных хостах
        // заголовок не кликабелен. Пункты списка работают корректно, так как
        // LazyColumn items изолированы.
        val modifier = GlanceModifier
            .fillMaxWidth()
            .background(primaryContainerColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)

        val headerModifier = if (!isBrokenClickableHost()) {
            modifier.clickable(
                actionStartActivity(
                    Intent(context, NewsWidgetConfigureActivity::class.java).apply {
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    }
                )
            )
        } else {
            modifier
        }

        Text(
            text = context.getString(R.string.news_widget_title),
            style = TextStyle(
                color = fixedColorProvider(onPrimaryContainerColor),
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            ),
            modifier = headerModifier,
        )
    }

    /**
     * Список новостей с цветными метками категорий.
     *
     * @param context Контекст приложения.
     * @param news Список пар: категория и новость.
     * @param themeColors Цветовая схема темы.
     * @param onBackgroundColor Цвет текста на фоне виджета.
     * @param announcementsColor Переопределение цвета метки категории «Анонсы».
     */
    @androidx.compose.runtime.Composable
    private fun NewsList(
        context: Context,
        news: List<Pair<NewsSubdivision, NewsPost>>,
        themeColors: ThemeColors,
        onBackgroundColor: Color,
        announcementsColor: String?,
    ) {
        LazyColumn(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            // Каждая item-лямбда должна эмитить ровно один элемент:
            // при нескольких детях Glance оборачивает их в Box, а такая вложенность
            // некорректно рендерится на части устройств (Transsion XOS, Android 10).
            // Отступ между пунктами задан нижним padding самого пункта.
            items(news.size) { index ->
                val (subdivision, post) = news[index]
                NewsItem(
                    context = context,
                    subdivision = subdivision,
                    post = post,
                    themeColors = themeColors,
                    onBackgroundColor = onBackgroundColor,
                    announcementsColor = announcementsColor,
                )
            }
        }
    }

    /**
     * Элемент новости. Строка с названием категории и датой окрашена в цвет категории.
     *
     * @param context Контекст приложения.
     * @param subdivision Категория новости.
     * @param post Новость.
     * @param themeColors Цветовая схема темы.
     * @param onBackgroundColor Цвет текста на фоне виджета.
     * @param announcementsColor Переопределение цвета метки категории «Анонсы».
     */
    @androidx.compose.runtime.Composable
    private fun NewsItem(
        context: Context,
        subdivision: NewsSubdivision,
        post: NewsPost,
        themeColors: ThemeColors,
        onBackgroundColor: Color,
        announcementsColor: String?,
    ) {
        val categoryColor = Color(hexToArgb(colorForSubdivision(subdivision, themeColors, announcementsColor)))

        val itemModifier = GlanceModifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)

        val url = post.relativeUrl
        val clickableModifier = if (url != null) {
            itemModifier.clickableWithSafeRipple(
                actionStartActivity(
                    Intent(Intent.ACTION_VIEW, url.toUri())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            )
        } else {
            itemModifier
        }

        // Пункт списка собран из двух Text в Column без цветной полосы-индикатора:
        // вместо неё цветом категории окрашена строка с названием категории и датой.
        Column(modifier = clickableModifier) {
            Text(
                text = post.title,
                style = TextStyle(
                    color = fixedColorProvider(onBackgroundColor),
                    fontSize = 13.sp,
                ),
                maxLines = 2,
            )
            Text(
                text = context.getString(subdivisionLabelRes(subdivision)) + " \u00b7 " + post.date,
                style = TextStyle(
                    color = fixedColorProvider(categoryColor),
                    fontSize = 12.sp,
                ),
                maxLines = 1,
            )
        }
    }

    /**
     * Возвращает строковый ресурс названия категории новости.
     *
     * @param subdivision Категория новости.
     * @return Идентификатор строкового ресурса.
     */
    private fun subdivisionLabelRes(subdivision: NewsSubdivision): Int {
        return when (subdivision) {
            NewsSubdivision.University -> R.string.news_subdivision_university
            NewsSubdivision.Exchange -> R.string.news_subdivision_exchange
            NewsSubdivision.PhD -> R.string.news_subdivision_phd
            NewsSubdivision.Dean -> R.string.news_subdivision_dean
            NewsSubdivision.Announcements -> R.string.news_subdivision_announcements
        }
    }

    /**
     * Состояние виджета при отсутствии данных конфигурации.
     *
     * @param context Контекст приложения.
     */
    @androidx.compose.runtime.Composable
    private fun EmptyState(context: Context) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFFFEFBFF))
                .cornerRadius(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = context.getString(R.string.news_widget_error),
                style = TextStyle(
                    color = fixedColorProvider(Color(0xFFBA1A1A)),
                    fontSize = 14.sp,
                ),
            )
        }
    }

    /**
     * Возвращает HEX-цвет метки для категории новости.
     *
     * @param subdivision Категория новости.
     * @param themeColors Цветовая схема темы.
     * @return HEX-строка цвета.
     */
    private fun colorForSubdivision(
        subdivision: NewsSubdivision,
        themeColors: ThemeColors,
        announcementsColor: String?,
    ): String {
        return when (subdivision) {
            NewsSubdivision.University -> themeColors.primary
            NewsSubdivision.Exchange -> themeColors.tertiary
            NewsSubdivision.PhD -> themeColors.secondary
            NewsSubdivision.Dean -> themeColors.error
            NewsSubdivision.Announcements -> announcementsColor ?: themeColors.surfaceTint
        }
    }

    /**
     * Определяет, используется ли тёмная системная тема.
     *
     * @param context Контекст приложения.
     * @return true, если системная тема тёмная.
     */
    private fun isSystemDarkTheme(context: Context): Boolean {
        val nightMode = context.resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * Преобразует HEX-строку в ARGB-Int.
     *
     * @param hex HEX-строка цвета (например, "#FF0000").
     * @return Цвет в формате Int (ARGB).
     */
    private fun hexToArgb(hex: String): Int {
        return hex.toColorInt()
    }

    /**
     * Применяет clickable с безопасным rippleOverride для сломанных хостов.
     *
     * На устройствах Transsion (Infinix, TECNO, itel) с Android ниже 12 стандартный
     * ripple Glance отображается сплошным цветом и ломает вёрстку. Вместо отключения
     * clickable используется прозрачный drawable, чтобы сохранить кликабельность.
     *
     * @param action действие, выполняемое при нажатии.
     * @return модификатор с clickable и безопасным ripple.
     */
    private fun GlanceModifier.clickableWithSafeRipple(action: Action): GlanceModifier {
        val rippleRes = if (isBrokenClickableHost()) {
            R.drawable.glance_transparent_ripple
        } else {
            NoRippleOverride
        }
        return clickable(action, rippleOverride = rippleRes)
    }

    /**
     * Определяет хосты, на которых стандартный ripple Glance рендерится сломанно.
     *
     * На устройствах Transsion (Infinix, TECNO, itel) с Android ниже 12 Glance
     * оборачивает clickable-элементы в Box с ripple-изображением. Стандартный ripple
     * отображается сплошным цветом темы. На таких устройствах используется прозрачный
     * drawable как rippleOverride.
     *
     * @return true, если нужен прозрачный rippleOverride.
     */
    private fun isBrokenClickableHost(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) return false
        val device = (Build.BRAND + " " + Build.MANUFACTURER).lowercase()
        return listOf("infinix", "tecno", "itel").any { it in device }
    }

    private companion object {
        private const val TAG = "NewsGlanceWidget"
    }
}

private fun fixedColorProvider(color: Color): ColorProvider =
    @Suppress("RESTRICTED_API") ColorProvider(color)
