package com.overklassniy.stankinschedule.news.widget.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.domain.settings.KeyColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemeColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemePreset
import com.overklassniy.stankinschedule.core.ui.components.AppScaffold
import com.overklassniy.stankinschedule.core.ui.components.color.ColorPreference
import com.overklassniy.stankinschedule.core.ui.ext.parse
import com.overklassniy.stankinschedule.core.ui.ext.toHEX
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision
import com.overklassniy.stankinschedule.news.widget.domain.model.NewsWidgetData
import com.overklassniy.stankinschedule.news.widget.ui.R
import com.overklassniy.stankinschedule.news.widget.ui.configure.NewsWidgetConfigureViewModel

/**
 * Экран конфигурации виджета новостей.
 *
 * Формирует UI: верхняя панель, секция выбора категорий новостей,
 * секцию настройки темы виджета, секцию цвета текста и кнопку подтверждения.
 *
 * @param appWidgetId Идентификатор виджета, для которого выполняется конфигурация.
 * @param viewModel ViewModel экрана. Поставляет списки и сохраняет данные.
 * @param onBackPressed Обработчик возврата назад.
 * @param onNewsWidgetChanged Обработчик применения настроек виджета.
 * @param modifier Модификатор внешнего вида и расположения.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsWidgetConfigureScreen(
    appWidgetId: Int,
    viewModel: NewsWidgetConfigureViewModel,
    onBackPressed: () -> Unit,
    onNewsWidgetChanged: (appWidgetId: Int, data: NewsWidgetData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    AppScaffold(
        topBar = {
            NewsWidgetConfigureAppBar(
                onBackPressed = onBackPressed,
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
            modifier = Modifier
                .padding(innerPadding)
                .padding(Dimen.ContentPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
        ) {
            val currentWidgetData by viewModel.currentData.collectAsState()
            val subdivisions by viewModel.subdivisions.collectAsState()

            var selectedSubdivisions by remember(currentWidgetData) {
                mutableStateOf<Set<NewsSubdivision>>(emptySet())
            }
            var themePreset by remember(currentWidgetData) { mutableStateOf(ThemePreset.System) }
            var keyColors by remember(currentWidgetData) { mutableStateOf(KeyColors.lightDefault()) }
            var textColor by remember(currentWidgetData) { mutableStateOf<String?>(null) }
            var announcementsColor by remember(currentWidgetData) { mutableStateOf<String?>(null) }

            // Инициализация полей из сохранённых настроек виджета, если они есть.
            LaunchedEffect(currentWidgetData) {
                currentWidgetData?.let { data ->
                    selectedSubdivisions = data.selectedSubdivisions
                    themePreset = data.themePreset
                    keyColors = data.keyColors ?: KeyColors.lightDefault()
                    textColor = data.textColor
                    announcementsColor = data.announcementsColor
                }
            }

            // Загрузка сохранённых настроек виджета при входе на экран.
            LaunchedEffect(appWidgetId) {
                viewModel.loadConfigure(appWidgetId)
            }

            // Секция выбора категорий новостей
            WidgetPreferenceCategory(title = stringResource(R.string.news_widget_categories))
            WidgetCategoriesSection(
                subdivisions = subdivisions,
                selectedSubdivisions = selectedSubdivisions,
                onToggle = { subdivision ->
                    selectedSubdivisions = if (subdivision in selectedSubdivisions) {
                        selectedSubdivisions - subdivision
                    } else {
                        selectedSubdivisions + subdivision
                    }
                },
            )

            WidgetDivider()

            // Секция темы виджета
            WidgetPreferenceCategory(title = stringResource(R.string.news_widget_theme_title))
            WidgetPresetSection(
                currentPreset = themePreset,
                onPresetSelected = { themePreset = it },
            )

            // Ключевые цвета только для кастомного пресета
            if (themePreset == ThemePreset.Custom) {
                WidgetDivider()
                WidgetPreferenceCategory(title = stringResource(R.string.news_widget_theme_colors))
                WidgetKeyColorsSection(
                    keyColors = keyColors,
                    announcementsColor = announcementsColor,
                    onColorChanged = { name, hex ->
                        keyColors = when (name) {
                            "primary" -> keyColors.copy(primary = hex)
                            "secondary" -> keyColors.copy(secondary = hex)
                            "tertiary" -> keyColors.copy(tertiary = hex)
                            "background" -> keyColors.copy(background = hex)
                            "surface" -> keyColors.copy(surface = hex)
                            "error" -> keyColors.copy(error = hex)
                            else -> keyColors
                        }
                    },
                    onAnnouncementsColorChanged = { hex -> announcementsColor = hex },
                )
            }

            WidgetDivider()

            // Секция цвета текста виджета
            WidgetPreferenceCategory(title = stringResource(R.string.news_widget_text_color))
            WidgetTextColorSection(
                textColor = textColor,
                themePreset = themePreset,
                keyColors = keyColors,
                onColorChanged = { hex -> textColor = hex },
                onReset = { textColor = null },
            )

            Button(
                onClick = {
                    if (selectedSubdivisions.isNotEmpty()) {
                        val data = NewsWidgetData(
                            selectedSubdivisions = selectedSubdivisions,
                            themePreset = themePreset,
                            keyColors = if (themePreset == ThemePreset.Custom) keyColors else null,
                            textColor = textColor,
                            announcementsColor = announcementsColor,
                        )
                        onNewsWidgetChanged(appWidgetId, data)
                    }
                },
                enabled = selectedSubdivisions.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(
                        if (currentWidgetData == null) {
                            R.string.news_widget_add
                        } else {
                            R.string.news_widget_change
                        }
                    )
                )
            }
        }
    }
}

/**
 * Секция выбора категорий новостей через чекбоксы.
 *
 * @param subdivisions Полный список доступных категорий.
 * @param selectedSubdivisions Множество выбранных категорий.
 * @param onToggle Обработчик переключения категории.
 */
@Composable
private fun WidgetCategoriesSection(
    subdivisions: List<NewsSubdivision>,
    selectedSubdivisions: Set<NewsSubdivision>,
    onToggle: (NewsSubdivision) -> Unit,
) {
    for (subdivision in subdivisions) {
        val labelRes = when (subdivision) {
            NewsSubdivision.University -> R.string.news_subdivision_university
            NewsSubdivision.Exchange -> R.string.news_subdivision_exchange
            NewsSubdivision.PhD -> R.string.news_subdivision_phd
            NewsSubdivision.Dean -> R.string.news_subdivision_dean
            NewsSubdivision.Announcements -> R.string.news_subdivision_announcements
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle(subdivision) }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = subdivision in selectedSubdivisions,
                onCheckedChange = { onToggle(subdivision) },
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

/**
 * Секция выбора пресета темы виджета.
 *
 * @param currentPreset Текущий выбранный пресет.
 * @param onPresetSelected Обработчик выбора пресета.
 */
@Composable
private fun WidgetPresetSection(
    currentPreset: ThemePreset,
    onPresetSelected: (ThemePreset) -> Unit,
) {
    val presets = listOf(
        ThemePreset.System to R.string.news_widget_preset_system,
        ThemePreset.Light to R.string.news_widget_preset_light,
        ThemePreset.Dark to R.string.news_widget_preset_dark,
        ThemePreset.Custom to R.string.news_widget_preset_custom,
    )

    for ((preset, labelRes) in presets) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPresetSelected(preset) }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = currentPreset == preset,
                onClick = { onPresetSelected(preset) },
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

/**
 * Секция настройки ключевых цветов Material 3 для виджета.
 *
 * Включает цвета 5 категорий новостей и 2 цвета фона/поверхности.
 * Остальные цвета вычисляются автоматически через [ThemeColors.derive].
 *
 * @param keyColors Текущие ключевые цвета.
 * @param announcementsColor Текущий цвет метки категории «Анонсы» или null для surfaceTint.
 * @param onColorChanged Обработчик изменения цвета. Принимает имя цвета и HEX-значение.
 * @param onAnnouncementsColorChanged Обработчик изменения цвета метки «Анонсы».
 */
@Composable
private fun WidgetKeyColorsSection(
    keyColors: KeyColors,
    announcementsColor: String?,
    onColorChanged: (colorName: String, hex: String) -> Unit,
    onAnnouncementsColorChanged: (hex: String) -> Unit,
) {
    val defaultColors = KeyColors.lightDefault()
    val defaultAnnouncementsColor = defaultColors.primary
    val currentAnnouncementsColor = announcementsColor ?: keyColors.primary

    ColorPreference(
        title = stringResource(R.string.news_widget_color_primary),
        subtitle = "",
        color = Color.parse(keyColors.primary),
        defaultColor = Color.parse(defaultColors.primary),
        onColorChanged = { onColorChanged("primary", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.news_widget_color_tertiary),
        subtitle = "",
        color = Color.parse(keyColors.tertiary),
        defaultColor = Color.parse(defaultColors.tertiary),
        onColorChanged = { onColorChanged("tertiary", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.news_widget_color_secondary),
        subtitle = "",
        color = Color.parse(keyColors.secondary),
        defaultColor = Color.parse(defaultColors.secondary),
        onColorChanged = { onColorChanged("secondary", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.news_widget_color_error),
        subtitle = "",
        color = Color.parse(keyColors.error),
        defaultColor = Color.parse(defaultColors.error),
        onColorChanged = { onColorChanged("error", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.news_widget_color_announcements),
        subtitle = "",
        color = Color.parse(currentAnnouncementsColor),
        defaultColor = Color.parse(defaultAnnouncementsColor),
        onColorChanged = { onAnnouncementsColorChanged(it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.news_widget_color_background),
        subtitle = "",
        color = Color.parse(keyColors.background),
        defaultColor = Color.parse(defaultColors.background),
        onColorChanged = { onColorChanged("background", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.news_widget_color_surface),
        subtitle = "",
        color = Color.parse(keyColors.surface),
        defaultColor = Color.parse(defaultColors.surface),
        onColorChanged = { onColorChanged("surface", it.toHEX()) },
    )
}

/**
 * Секция настройки цвета текста виджета.
 *
 * Переопределяет onSurface и onBackground. Доступна для всех пресетов.
 *
 * @param textColor Текущий цвет текста или null для автоматического вычисления.
 * @param themePreset Текущий пресет темы виджета.
 * @param keyColors Ключевые цвета (для вычисления default в Custom).
 * @param onColorChanged Обработчик изменения цвета текста.
 * @param onReset Обработчик сброса к автоматическому вычислению.
 */
@Composable
private fun WidgetTextColorSection(
    textColor: String?,
    themePreset: ThemePreset,
    keyColors: KeyColors,
    onColorChanged: (hex: String) -> Unit,
    onReset: () -> Unit,
) {
    val defaultOnSurface = when (themePreset) {
        ThemePreset.Dark -> ThemeColors.darkDefault().onSurface
        ThemePreset.Custom -> ThemeColors.derive(keyColors).onSurface
        else -> ThemeColors.lightDefault().onSurface
    }

    val currentColor = textColor ?: defaultOnSurface

    ColorPreference(
        title = stringResource(R.string.news_widget_text_color),
        subtitle = "",
        color = Color.parse(currentColor),
        defaultColor = Color.parse(defaultOnSurface),
        onColorChanged = { onColorChanged(it.toHEX()) },
    )

    if (textColor != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                onClick = onReset,
            ) {
                Text(text = stringResource(R.string.news_widget_text_color_reset))
            }
        }
    }
}

/**
 * Заголовок секции настроек виджета.
 *
 * @param title Заголовок секции.
 */
@Composable
private fun WidgetPreferenceCategory(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        modifier = modifier
            .padding(
                start = 16.dp,
                top = 16.dp,
                end = 8.dp,
                bottom = 8.dp,
            ),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleSmall,
    )
}

/**
 * Разделитель между секциями настроек виджета.
 */
@Composable
private fun WidgetDivider() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
