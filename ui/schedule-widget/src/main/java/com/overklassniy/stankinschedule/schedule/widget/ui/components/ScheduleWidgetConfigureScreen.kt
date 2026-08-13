package com.overklassniy.stankinschedule.schedule.widget.ui.components

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
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
import com.overklassniy.stankinschedule.core.ui.components.OutlinedSelectField
import com.overklassniy.stankinschedule.core.ui.components.color.ColorPreference
import com.overklassniy.stankinschedule.core.ui.ext.parse
import com.overklassniy.stankinschedule.core.ui.ext.toHEX
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.ui.toColor
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorType
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleItem
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.overklassniy.stankinschedule.schedule.widget.ui.R
import com.overklassniy.stankinschedule.schedule.widget.ui.configure.ScheduleWidgetConfigureViewModel
import com.overklassniy.stankinschedule.core.ui.R as R_core
import com.overklassniy.stankinschedule.schedule.core.ui.R as R_schedule_core

/**
 * Экран конфигурации виджета расписания.
 *
 * Формирует UI: верхняя панель, поля выбора расписания и подгруппы,
 * переключатель отображения подгруппы, секцию настройки темы виджета,
 * секцию настройки цветов пар и кнопку подтверждения.
 *
 * @param appWidgetId Идентификатор виджета, для которого выполняется конфигурация.
 * @param viewModel ViewModel экрана. Поставляет списки и сохраняет данные.
 * @param onBackPressed Обработчик возврата назад.
 * @param onScheduleWidgetChanged Обработчик применения настроек виджета.
 * @param modifier Модификатор внешнего вида и расположения.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleWidgetConfigureScreen(
    appWidgetId: Int,
    viewModel: ScheduleWidgetConfigureViewModel,
    onBackPressed: () -> Unit,
    onScheduleWidgetChanged: (appWidgetId: Int, data: ScheduleWidgetData) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    AppScaffold(
        topBar = {
            ScheduleWidgetConfigureAppBar(
                onBackPressed = onBackPressed,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
            modifier = Modifier
                .padding(innerPadding)
                .padding(Dimen.ContentPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            val currentWidgetData by viewModel.currentData.collectAsState()

            val schedules by viewModel.schedules.collectAsState()
            var currentSchedule by remember { mutableStateOf(ScheduleItem.NO_ITEM) }
            var currentSubgroup by remember(currentWidgetData) { mutableStateOf(Subgroup.COMMON) }
            var isShowSubgroup by remember(currentWidgetData) { mutableStateOf(true) }

            var themePreset by remember(currentWidgetData) { mutableStateOf(ThemePreset.System) }
            var keyColors by remember(currentWidgetData) { mutableStateOf(KeyColors.lightDefault()) }
            var pairColorGroup by remember(currentWidgetData) { mutableStateOf(PairColorGroup.default()) }
            var textColor by remember(currentWidgetData) { mutableStateOf<String?>(null) }

            // Инициализация полей из сохраненных настроек виджета, если они есть.
            LaunchedEffect(currentWidgetData) {
                currentWidgetData?.let { data ->
                    currentSchedule = ScheduleItem(
                        data.scheduleName,
                        data.scheduleId
                    )
                    currentSubgroup = data.subgroup
                    isShowSubgroup = data.display
                    themePreset = data.themePreset
                    keyColors = data.keyColors ?: KeyColors.lightDefault()
                    pairColorGroup = data.pairColorGroup
                    textColor = data.textColor
                }
            }

            // Загрузка сохраненных настроек виджета при входе на экран.
            LaunchedEffect(appWidgetId) {
                viewModel.loadConfigure(appWidgetId)
            }

            OutlinedSelectField(
                value = currentSchedule,
                onValueChanged = { currentSchedule = it },
                items = schedules,
                menuLabel = { it.scheduleName },
                label = { Text(text = stringResource(R.string.widget_schedule)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedSelectField(
                value = currentSubgroup,
                onValueChanged = { currentSubgroup = it },
                items = Subgroup.entries,
                menuLabel = {
                    val id = when (it) {
                        Subgroup.COMMON -> R_schedule_core.string.subgroup_common
                        Subgroup.A -> R_schedule_core.string.subgroup_a
                        Subgroup.B -> R_schedule_core.string.subgroup_b
                    }
                    stringResource(id)
                },
                label = { Text(text = stringResource(R.string.widget_subgroup)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.widget_show_subgroup),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                )

                Switch(
                    checked = isShowSubgroup,
                    onCheckedChange = { isShowSubgroup = it }
                )
            }

            WidgetDivider()

            // Секция темы виджета
            WidgetPreferenceCategory(title = stringResource(R.string.widget_theme_title))
            WidgetPresetSection(
                currentPreset = themePreset,
                onPresetSelected = { themePreset = it },
            )

            // Ключевые цвета только для кастомного пресета
            if (themePreset == ThemePreset.Custom) {
                WidgetDivider()
                WidgetPreferenceCategory(title = stringResource(R.string.widget_theme_colors))
                WidgetKeyColorsSection(
                    keyColors = keyColors,
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
                )
            }

            WidgetDivider()

            // Секция цвета текста виджета
            WidgetPreferenceCategory(title = stringResource(R.string.widget_text_color))
            WidgetTextColorSection(
                textColor = textColor,
                themePreset = themePreset,
                keyColors = keyColors,
                onColorChanged = { hex -> textColor = hex },
                onReset = { textColor = null },
            )

            WidgetDivider()

            // Секция цветов пар
            WidgetPreferenceCategory(title = stringResource(R.string.widget_pair_colors))
            WidgetPairColorsSection(
                pairColorGroup = pairColorGroup,
                onColorChanged = { hex, type ->
                    pairColorGroup = when (type) {
                        PairColorType.Lecture -> pairColorGroup.copy(lectureColor = hex)
                        PairColorType.Seminar -> pairColorGroup.copy(seminarColor = hex)
                        PairColorType.Laboratory -> pairColorGroup.copy(laboratoryColor = hex)
                        PairColorType.SubgroupA -> pairColorGroup.copy(subgroupAColor = hex)
                        PairColorType.SubgroupB -> pairColorGroup.copy(subgroupBColor = hex)
                    }
                },
            )

            Button(
                onClick = {
                    if (currentSchedule != ScheduleItem.NO_ITEM) {
                        val data = ScheduleWidgetData(
                            scheduleName = currentSchedule.scheduleName,
                            scheduleId = currentSchedule.scheduleId,
                            subgroup = currentSubgroup,
                            display = isShowSubgroup,
                            themePreset = themePreset,
                            keyColors = if (themePreset == ThemePreset.Custom) keyColors else null,
                            pairColorGroup = pairColorGroup,
                            textColor = textColor,
                        )
                        onScheduleWidgetChanged(appWidgetId, data)
                    }
                },
                enabled = currentSchedule != ScheduleItem.NO_ITEM,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        if (currentWidgetData == null) {
                            R.string.widget_add
                        } else {
                            R.string.widget_change
                        }
                    )
                )
            }
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
        ThemePreset.System to R.string.widget_preset_system,
        ThemePreset.Light to R.string.widget_preset_light,
        ThemePreset.Dark to R.string.widget_preset_dark,
        ThemePreset.Custom to R.string.widget_preset_custom,
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
 * Секция настройки 6 ключевых цветов Material 3 для виджета.
 *
 * Остальные 22 цвета вычисляются автоматически через [ThemeColors.derive].
 *
 * @param keyColors Текущие ключевые цвета.
 * @param onColorChanged Обработчик изменения цвета. Принимает имя цвета и HEX-значение.
 */
@Composable
private fun WidgetKeyColorsSection(
    keyColors: KeyColors,
    onColorChanged: (colorName: String, hex: String) -> Unit,
) {
    val defaultColors = KeyColors.lightDefault()

    ColorPreference(
        title = stringResource(R.string.widget_color_primary),
        subtitle = stringResource(R_core.string.color_summary),
        color = Color.parse(keyColors.primary),
        defaultColor = Color.parse(defaultColors.primary),
        onColorChanged = { onColorChanged("primary", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.widget_color_secondary),
        subtitle = stringResource(R_core.string.color_summary),
        color = Color.parse(keyColors.secondary),
        defaultColor = Color.parse(defaultColors.secondary),
        onColorChanged = { onColorChanged("secondary", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.widget_color_tertiary),
        subtitle = stringResource(R_core.string.color_summary),
        color = Color.parse(keyColors.tertiary),
        defaultColor = Color.parse(defaultColors.tertiary),
        onColorChanged = { onColorChanged("tertiary", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.widget_color_background),
        subtitle = stringResource(R_core.string.color_summary),
        color = Color.parse(keyColors.background),
        defaultColor = Color.parse(defaultColors.background),
        onColorChanged = { onColorChanged("background", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.widget_color_surface),
        subtitle = stringResource(R_core.string.color_summary),
        color = Color.parse(keyColors.surface),
        defaultColor = Color.parse(defaultColors.surface),
        onColorChanged = { onColorChanged("surface", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.widget_color_error),
        subtitle = stringResource(R_core.string.color_summary),
        color = Color.parse(keyColors.error),
        defaultColor = Color.parse(defaultColors.error),
        onColorChanged = { onColorChanged("error", it.toHEX()) },
    )
}

/**
 * Секция настройки цветов пар для виджета.
 *
 * @param pairColorGroup Группа цветов пар.
 * @param onColorChanged Обработчик изменения цвета пары.
 */
@Composable
private fun WidgetPairColorsSection(
    pairColorGroup: PairColorGroup,
    onColorChanged: (hex: String, type: PairColorType) -> Unit,
) {
    val pairColors = remember(pairColorGroup) { pairColorGroup.toColor() }
    val pairColorsDefault = PairColorGroup.default().toColor()

    ColorPreference(
        title = stringResource(R.string.widget_lecture_color),
        subtitle = stringResource(R_core.string.color_summary),
        color = pairColors.lectureColor,
        defaultColor = pairColorsDefault.lectureColor,
        onColorChanged = { onColorChanged(it.toHEX(), PairColorType.Lecture) },
    )
    ColorPreference(
        title = stringResource(R.string.widget_seminar_color),
        subtitle = stringResource(R_core.string.color_summary),
        color = pairColors.seminarColor,
        defaultColor = pairColorsDefault.seminarColor,
        onColorChanged = { onColorChanged(it.toHEX(), PairColorType.Seminar) },
    )
    ColorPreference(
        title = stringResource(R.string.widget_laboratory_color),
        subtitle = stringResource(R_core.string.color_summary),
        color = pairColors.laboratoryColor,
        defaultColor = pairColorsDefault.laboratoryColor,
        onColorChanged = { onColorChanged(it.toHEX(), PairColorType.Laboratory) },
    )
    ColorPreference(
        title = stringResource(R.string.widget_subgroup_a_color),
        subtitle = stringResource(R_core.string.color_summary),
        color = pairColors.subgroupAColor,
        defaultColor = pairColorsDefault.subgroupAColor,
        onColorChanged = { onColorChanged(it.toHEX(), PairColorType.SubgroupA) },
    )
    ColorPreference(
        title = stringResource(R.string.widget_subgroup_b_color),
        subtitle = stringResource(R_core.string.color_summary),
        color = pairColors.subgroupBColor,
        defaultColor = pairColorsDefault.subgroupBColor,
        onColorChanged = { onColorChanged(it.toHEX(), PairColorType.SubgroupB) },
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
        title = stringResource(R.string.widget_text_color),
        subtitle = stringResource(R_core.string.color_summary),
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
                Text(text = stringResource(R.string.widget_text_color_reset))
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