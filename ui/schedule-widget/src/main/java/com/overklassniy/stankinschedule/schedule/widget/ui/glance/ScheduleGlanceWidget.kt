package com.overklassniy.stankinschedule.schedule.widget.ui.glance

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
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.action.Action
import androidx.glance.action.NoRippleOverride
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.currentState
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.overklassniy.stankinschedule.core.ui.ext.toTitleCase
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.overklassniy.stankinschedule.schedule.widget.ui.R
import com.overklassniy.stankinschedule.schedule.widget.ui.configure.ScheduleWidgetConfigureActivity
import com.overklassniy.stankinschedule.schedule.widget.ui.utils.ScheduleDeepLink
import com.overklassniy.stankinschedule.schedule.core.ui.R as R_schedule_core
import dagger.hilt.android.EntryPointAccessors
import org.joda.time.LocalDate

/**
 * Glance-виджет расписания.
 *
 * Использует [PreferencesGlanceStateDefinition] для хранения конфигурации каждого
 * экземпляра виджета. Состояние сериализуется через [WidgetStateKeys].
 *
 * При первом рендеринге, если состояние пусто, выполняется одноразовая миграция
 * из legacy SharedPreferences.
 */
class ScheduleGlanceWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Single

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            Log.d(TAG, "provideGlance: start, id=$id")

            // Миграция при первом запуске, если prefs пусты
            val prefs = getAppWidgetState<Preferences>(context, id)
            val initialData = WidgetStateKeys.toData(prefs)
            if (initialData == null) {
                Log.d(TAG, "provideGlance: no data, attempting migration")
                migrateFromLegacy(context, id)
            }

            val entryPoint = EntryPointAccessors.fromApplication(
                context,
                ScheduleGlanceEntryPoint::class.java
            )
            val useCase = entryPoint.scheduleWidgetUseCase()
            Log.d(TAG, "provideGlance: useCase obtained")

            val today = LocalDate.now()
            val manager = GlanceAppWidgetManager(context)
            val appWidgetId = manager.getAppWidgetId(id)

            // Записываем isDark в state, чтобы при смене темы MainApplication
            // мог обновить этот ключ и триггерить рекомпозицию через currentState
            val initialIsDark = isSystemDarkTheme(context)
            updateAppWidgetState(context, id) { prefs ->
                prefs[WidgetStateKeys.isDarkTheme] = initialIsDark
            }

            // Состояние читается внутри provideContent через currentState,
            // чтобы рекомпозиция получала свежие данные после update()
            provideContent {
                Log.d(TAG, "provideContent: composing, appWidgetId=$appWidgetId")
                val currentPrefs = currentState<Preferences>()
                val data = WidgetStateKeys.toData(currentPrefs)
                // isDark из state, fallback на initialIsDark — currentState может
                // читать snapshot до записи updateAppWidgetState при первом рендере
                val isDark = currentPrefs[WidgetStateKeys.isDarkTheme] ?: initialIsDark
                Log.d(TAG, "provideContent: data from currentState=$data")

                // Загрузка дней расписания при изменении данных
                var days by remember(data?.scheduleId) {
                    mutableStateOf<List<List<PairModel>>>(emptyList())
                }

                LaunchedEffect(data?.scheduleId) {
                    if (data != null) {
                        Log.d(TAG, "LaunchedEffect: loading days, scheduleId=${data.scheduleId}")
                        days = useCase.scheduleDays(
                            data.scheduleId,
                            data.subgroup,
                            today,
                            today.plusDays(7)
                        )
                        Log.d(TAG, "LaunchedEffect: days loaded, size=${days.size}")
                    }
                }

                WidgetContent(LocalContext.current, appWidgetId, data, days, today, isDark)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "provideGlance: FATAL ERROR", e)
            try {
                EntryPointAccessors.fromApplication(context, ScheduleGlanceEntryPoint::class.java)
                    .crashReporter()
                    .recordException(e)
            } catch (reportError: Throwable) {
                Log.e(TAG, "provideGlance: failed to report error", reportError)
            }
            throw e
        }
    }

    /**
     * Одноразовая миграция из legacy SharedPreferences в Glance state.
     *
     * @param context Контекст приложения.
     * @param id Идентификатор Glance-виджета.
     * @return Данные виджета из legacy-хранилища или null.
     */
    private suspend fun migrateFromLegacy(context: Context, id: GlanceId): ScheduleWidgetData? {
        return try {
            val manager = GlanceAppWidgetManager(context)
            val appWidgetId = manager.getAppWidgetId(id)
            Log.d(TAG, "migrateFromLegacy: appWidgetId=$appWidgetId")
            val legacyPref = com.overklassniy.stankinschedule.widget.data.repository.ScheduleWidgetPreferenceImpl(context)
            val data = legacyPref.loadData(appWidgetId)
            Log.d(TAG, "migrateFromLegacy: legacy data=$data")
            if (data == null) return null

            updateAppWidgetState(context, id) { prefs ->
                WidgetStateKeys.writeToPrefs(prefs, data)
            }
            data
        } catch (e: Exception) {
            Log.e(TAG, "migrateFromLegacy: error", e)
            null
        }
    }

    /**
     * Содержимое виджета: заголовок, список дней с парами.
     *
     * @param context Контекст приложения.
     * @param data Данные конфигурации виджета или null.
     * @param days Список списков пар по дням.
     * @param today Текущая дата.
     * @param isDark Признак тёмной системной темы.
     */
    @androidx.compose.runtime.Composable
    private fun WidgetContent(
        context: Context,
        appWidgetId: Int,
        data: ScheduleWidgetData?,
        days: List<List<PairModel>>,
        today: LocalDate,
        isDark: Boolean,
    ) {
        if (data == null) {
            EmptyState(context)
            return
        }

        val themeColors = data.themeColors(isDark)
        val pairColors = data.pairColorGroup

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
                data = data,
                primaryContainerColor = primaryContainerColor,
                onPrimaryContainerColor = onPrimaryContainerColor,
            )

            if (days.isEmpty()) {
                Text(
                    text = context.getString(R.string.widget_no_pairs),
                    style = TextStyle(
                        color = fixedColorProvider(onBackgroundColor),
                        fontSize = 14.sp,
                    ),
                    modifier = GlanceModifier.padding(16.dp),
                )
            } else {
                DaysList(
                    context = context,
                    days = days,
                    today = today,
                    scheduleId = data.scheduleId,
                    pairColors = pairColors,
                    onBackgroundColor = onBackgroundColor,
                    isDark = isDark,
                )
            }
        }
    }

    /**
     * Заголовок виджета с названием расписания. Нажатие открывает экран настройки виджета.
     *
     * Заголовок представлен одним [Text], а не Row с иконкой: вложенные Row
     * с несколькими дочерними элементами некорректно рендерятся на части устройств
     * (например, Transsion XOS на Android 10). Переход к расписанию доступен
     * через заголовки дней в списке.
     *
     * @param context Контекст приложения.
     * @param appWidgetId Идентификатор виджета.
     * @param data Данные конфигурации виджета.
     * @param primaryContainerColor Цвет фона заголовка.
     * @param onPrimaryContainerColor Цвет текста заголовка.
     */
    @androidx.compose.runtime.Composable
    private fun Header(
        context: Context,
        appWidgetId: Int,
        data: ScheduleWidgetData,
        primaryContainerColor: Color,
        onPrimaryContainerColor: Color,
    ) {
        val displayName = displayScheduleName(context, data)

        // На Transsion pre-S Glance оборачивает clickable-элементы в Box с ripple-
        // изображением. Для заголовка внутри корневого Column эта Box-обёртка
        // вызывает перекрытие с контентом ниже. Поэтому на сломанных хостах
        // заголовок не кликабелен. Заголовки дней внутри LazyColumn работают
        // корректно, так как items изолированы.
        val modifier = GlanceModifier
            .fillMaxWidth()
            .background(primaryContainerColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)

        val headerModifier = if (!isBrokenClickableHost()) {
            modifier.clickable(
                actionStartActivity(
                    Intent(context, ScheduleWidgetConfigureActivity::class.java).apply {
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    }
                )
            )
        } else {
            modifier
        }

        Text(
            text = displayName,
            style = TextStyle(
                color = fixedColorProvider(onPrimaryContainerColor),
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            ),
            modifier = headerModifier,
        )
    }

    /**
     * Список дней с парами.
     *
     * @param context Контекст приложения.
     * @param days Список списков пар по дням.
     * @param today Текущая дата.
     * @param scheduleId ID расписания для deep-link.
     * @param pairColors Цвета пар.
     * @param onBackgroundColor Цвет текста на фоне виджета.
     * @param isDark Признак тёмной системной темы для тонирования фона пар.
     */
    @androidx.compose.runtime.Composable
    private fun DaysList(
        context: Context,
        days: List<List<PairModel>>,
        today: LocalDate,
        scheduleId: Long,
        pairColors: PairColorGroup,
        onBackgroundColor: Color,
        isDark: Boolean,
    ) {
        // Список собран из плоских пунктов (заголовок дня и пары как отдельные items):
        // многоуровневая вложенность контейнеров внутри пунктов списка
        // некорректно рендерится на части устройств (Transsion XOS, Android 10).
        LazyColumn(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            days.forEachIndexed { index, pairs ->
                val date = today.plusDays(index)
                val dayTitle = date.toString("EE, dd MMMM").toTitleCase()

                item {
                    val dayAction = actionStartActivity(
                        ScheduleDeepLink.viewerIntent(scheduleId, date)
                    )

                    Text(
                        text = dayTitle,
                        style = TextStyle(
                            color = fixedColorProvider(onBackgroundColor),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        ),
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .clickableWithSafeRipple(dayAction),
                    )
                }

                if (pairs.isEmpty()) {
                    item {
                        Text(
                            text = context.getString(R.string.widget_no_pairs),
                            style = TextStyle(
                                color = fixedColorProvider(onBackgroundColor),
                                fontSize = 13.sp,
                            ),
                            modifier = GlanceModifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    val pairAction = actionStartActivity(
                        ScheduleDeepLink.viewerIntent(scheduleId, date)
                    )
                    for (pair in pairs) {
                        item {
                            PairItem(
                                pair = pair,
                                pairColors = pairColors,
                                onBackgroundColor = onBackgroundColor,
                                isDark = isDark,
                                onClickAction = pairAction,
                            )
                        }
                    }
                }

                if (index < days.lastIndex) {
                    item {
                        Spacer(modifier = GlanceModifier.height(4.dp))
                    }
                }
            }
        }
    }

    /**
     * Элемент пары. Строка имеет тонированный фон цвета типа пары
     * вместо цветной полосы-индикатора или окрашенного текста.
     *
     * @param pair Модель пары.
     * @param pairColors Цвета пар.
     * @param onBackgroundColor Цвет текста на фоне виджета.
     * @param isDark Признак тёмной системной темы для тонирования фона.
     * @param onClickAction Действие при нажатии на пару.
     */
    @androidx.compose.runtime.Composable
    private fun PairItem(
        pair: PairModel,
        pairColors: PairColorGroup,
        onBackgroundColor: Color,
        isDark: Boolean,
        onClickAction: Action,
    ) {
        val tintColor = tintColorForPair(colorForPair(pair, pairColors), isDark)

        val timeWithClassroom = if (pair.classroom.isNotEmpty()) {
            pair.time.toString() + ", " + pair.classroom
        } else {
            pair.time.toString()
        }

        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(tintColor)
                .cornerRadius(8.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickableWithSafeRipple(onClickAction)
        ) {
            Text(
                text = pair.title,
                style = TextStyle(
                    color = fixedColorProvider(onBackgroundColor),
                    fontSize = 13.sp,
                ),
                maxLines = 2,
            )
            Text(
                text = timeWithClassroom,
                style = TextStyle(
                    color = fixedColorProvider(onBackgroundColor),
                    fontSize = 12.sp,
                ),
                maxLines = 1,
            )
        }
    }

    /**
     * Состояние виджета при отсутствии данных конфигурации.
     *
     * @param context Контекст приложения.
     */
    @androidx.compose.runtime.Composable
    private fun EmptyState(context: Context) {
        Log.d(TAG, "EmptyState: composing")
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFFFEFBFF))
                .cornerRadius(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = context.getString(R.string.widget_error),
                style = TextStyle(
                    color = fixedColorProvider(Color(0xFFBA1A1A)),
                    fontSize = 14.sp,
                ),
            )
        }
        Log.d(TAG, "EmptyState: composed")
    }

    /**
     * Формирует отображаемое имя расписания с постфиксом подгруппы.
     *
     * @param context Контекст приложения.
     * @param data Данные виджета.
     * @return Строка имени расписания.
     */
    private fun displayScheduleName(
        context: Context,
        data: ScheduleWidgetData
    ): String {
        if (!data.display || data.subgroup == Subgroup.COMMON) return data.scheduleName

        val postfix = when (data.subgroup) {
            Subgroup.A -> context.getString(R_schedule_core.string.subgroup_a)
            Subgroup.B -> context.getString(R_schedule_core.string.subgroup_b)
            else -> ""
        }
        return data.scheduleName + " " + postfix
    }

    /**
     * Определяет цвет индикатора для пары по её типу и подгруппе.
     *
     * @param pair Модель пары.
     * @param pairColors Группа цветов пар.
     * @return Цвет индикатора в формате [Color].
     */
    private fun colorForPair(
        pair: PairModel,
        pairColorGroup: PairColorGroup
    ): Color {
        val hex = when (pair.type) {
            Type.LECTURE -> pairColorGroup.lectureColor
            Type.SEMINAR -> pairColorGroup.seminarColor
            Type.LABORATORY -> {
                when (pair.subgroup) {
                    Subgroup.A -> pairColorGroup.subgroupAColor
                    Subgroup.B -> pairColorGroup.subgroupBColor
                    else -> pairColorGroup.laboratoryColor
                }
            }
        }
        return Color(hexToArgb(hex))
    }

    /**
     * Вычисляет тонированный цвет фона для строки пары.
     *
     * Светлая тема: цвет пары смешивается с белым (70% белого) — отчётливый светлый оттенок.
     * Тёмная тема: цвет пары затемняется до 50% — тёмный оттенок с выраженным тоном.
     *
     * @param color Базовый цвет типа пары.
     * @param isDark Признак тёмной системной темы.
     * @return Тонированный цвет для фона строки пары.
     */
    private fun tintColorForPair(color: Color, isDark: Boolean): Color {
        return if (isDark) {
            Color(
                (color.red * 0.5f * 255).toInt(),
                (color.green * 0.5f * 255).toInt(),
                (color.blue * 0.5f * 255).toInt(),
                255,
            )
        } else {
            Color(
                (color.red * 0.3f * 255 + 255 * 0.7f).toInt(),
                (color.green * 0.3f * 255 + 255 * 0.7f).toInt(),
                (color.blue * 0.3f * 255 + 255 * 0.7f).toInt(),
                255,
            )
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
        private const val TAG = "ScheduleGlanceWidget"
    }
}

private fun fixedColorProvider(color: Color): ColorProvider =
    @Suppress("RESTRICTED_API") ColorProvider(color)