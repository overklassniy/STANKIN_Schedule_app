package com.overklassniy.stankinschedule.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.domain.settings.KeyColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemeColors
import com.overklassniy.stankinschedule.core.domain.settings.ThemePreset
import com.overklassniy.stankinschedule.core.ui.ext.parse
import com.overklassniy.stankinschedule.core.ui.ext.toHEX
import com.overklassniy.stankinschedule.schedule.core.ui.toColor
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorType
import com.overklassniy.stankinschedule.settings.ui.components.PreferenceCategory
import com.overklassniy.stankinschedule.settings.ui.components.PreferenceDivider
import com.overklassniy.stankinschedule.settings.ui.components.SettingsScaffold
import com.overklassniy.stankinschedule.core.ui.components.color.ColorPreference

/**
 * Экран настройки темы оформления.
 *
 * Содержит три секции: пресеты, ключевые цвета Material 3 и цвета пар.
 * Ключевые цвета (primary, secondary, tertiary, background, surface, error)
 * настраиваются пользователем, остальные 22 цвета вычисляются автоматически.
 *
 * @param viewModel ViewModel с состоянием настроек темы.
 * @param onBackPressed Обработчик нажатия кнопки «Назад».
 * @param modifier Модификатор для внешнего контейнера.
 */
@Composable
fun ThemeSettingsScreen(
    viewModel: SettingsViewModel,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsScaffold(
        title = stringResource(R.string.theme_settings_title),
        onBackPressed = onBackPressed,
        modifier = modifier
    ) {
        val themePreset by viewModel.themePreset.collectAsState()
        val themeColors by viewModel.themeColors.collectAsState(ThemeColors.lightDefault())
        val pairColorGroup by viewModel.pairColorGroup.collectAsState(PairColorGroup.default())

        // Секция 1: Пресеты
        PreferenceCategory(title = stringResource(R.string.theme_presets))
        PresetSection(
            currentPreset = themePreset,
            onPresetSelected = { viewModel.setThemePreset(it) },
            onResetToPreset = { viewModel.resetThemeColors(it) },
        )

        PreferenceDivider()

        // Секция 2: Ключевые цвета Material 3
        PreferenceCategory(title = stringResource(R.string.theme_material_colors))
        KeyColorsSection(
            themeColors = themeColors,
            onColorChanged = { name, hex -> viewModel.setThemeColor(name, hex) },
        )

        PreferenceDivider()

        // Секция 2.1: Цвет текста
        PreferenceCategory(title = stringResource(R.string.theme_text_color))
        TextColorSection(
            themeColors = themeColors,
            onColorChanged = { hex -> viewModel.setThemeTextColor(hex) },
            onReset = { viewModel.resetThemeTextColor() },
        )

        PreferenceDivider()

        // Секция 3: Цвета пар
        PreferenceCategory(title = stringResource(R.string.theme_pair_colors))
        PairColorsSection(
            pairColorGroup = pairColorGroup,
            onColorChanged = { hex, type -> viewModel.setPairColor(hex, type) },
        )
    }
}

/**
 * Секция выбора пресета темы.
 *
 * @param currentPreset Текущий выбранный пресет.
 * @param onPresetSelected Обработчик выбора пресета.
 * @param onResetToPreset Обработчик сброса цветов к пресету.
 */
@Composable
private fun PresetSection(
    currentPreset: ThemePreset?,
    onPresetSelected: (ThemePreset) -> Unit,
    onResetToPreset: (ThemePreset) -> Unit,
) {
    val presets = listOf(
        ThemePreset.System to R.string.theme_preset_system,
        ThemePreset.Light to R.string.theme_preset_light,
        ThemePreset.Dark to R.string.theme_preset_dark,
        ThemePreset.Custom to R.string.theme_preset_custom,
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
            if (preset == ThemePreset.Custom) {
                Column {
                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = stringResource(R.string.theme_preset_custom_summary),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    )
                }
            } else {
                Text(
                    text = stringResource(labelRes),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }

    // Кнопка сброса к пресету
    if (currentPreset == ThemePreset.Custom) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                onClick = { onResetToPreset(ThemePreset.System) },
            ) {
                Text(text = stringResource(R.string.theme_reset_to_preset))
            }
        }
    }
}

/**
 * Секция настройки 6 ключевых цветов Material 3.
 *
 * Остальные 22 цвета вычисляются автоматически через [ThemeColors.derive].
 *
 * @param themeColors Текущие цвета темы (полная схема из 28 цветов).
 * @param onColorChanged Обработчик изменения цвета. Принимает имя цвета и HEX-значение.
 */
@Composable
private fun KeyColorsSection(
    themeColors: ThemeColors,
    onColorChanged: (colorName: String, hex: String) -> Unit,
) {
    val defaultColors = KeyColors.lightDefault()

    ColorPreference(
        title = stringResource(R.string.theme_color_primary),
        subtitle = stringResource(R.string.color_summary),
        color = Color.parse(themeColors.primary),
        defaultColor = Color.parse(defaultColors.primary),
        onColorChanged = { onColorChanged("primary", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.theme_color_secondary),
        subtitle = stringResource(R.string.color_summary),
        color = Color.parse(themeColors.secondary),
        defaultColor = Color.parse(defaultColors.secondary),
        onColorChanged = { onColorChanged("secondary", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.theme_color_tertiary),
        subtitle = stringResource(R.string.color_summary),
        color = Color.parse(themeColors.tertiary),
        defaultColor = Color.parse(defaultColors.tertiary),
        onColorChanged = { onColorChanged("tertiary", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.theme_color_background),
        subtitle = stringResource(R.string.color_summary),
        color = Color.parse(themeColors.background),
        defaultColor = Color.parse(defaultColors.background),
        onColorChanged = { onColorChanged("background", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.theme_color_surface),
        subtitle = stringResource(R.string.color_summary),
        color = Color.parse(themeColors.surface),
        defaultColor = Color.parse(defaultColors.surface),
        onColorChanged = { onColorChanged("surface", it.toHEX()) },
    )
    ColorPreference(
        title = stringResource(R.string.theme_color_error),
        subtitle = stringResource(R.string.color_summary),
        color = Color.parse(themeColors.error),
        defaultColor = Color.parse(defaultColors.error),
        onColorChanged = { onColorChanged("error", it.toHEX()) },
    )
}

/**
 * Секция настройки цвета текста.
 *
 * Переопределяет onSurface и onBackground. Доступна для всех пресетов.
 *
 * @param themeColors Текущие цвета темы.
 * @param onColorChanged Обработчик изменения цвета текста.
 * @param onReset Обработчик сброса к автоматическому вычислению.
 */
@Composable
private fun TextColorSection(
    themeColors: ThemeColors,
    onColorChanged: (hex: String) -> Unit,
    onReset: () -> Unit,
) {
    val defaultOnSurface = ThemeColors.lightDefault().onSurface

    ColorPreference(
        title = stringResource(R.string.theme_text_color),
        subtitle = stringResource(R.string.theme_text_color_summary),
        color = Color.parse(themeColors.onSurface),
        defaultColor = Color.parse(defaultOnSurface),
        onColorChanged = { onColorChanged(it.toHEX()) },
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(
            onClick = onReset,
        ) {
            Text(text = stringResource(R.string.theme_text_color_reset))
        }
    }
}

/**
 * Секция настройки цветов пар.
 *
 * @param pairColorGroup Группа цветов пар.
 * @param onColorChanged Обработчик изменения цвета пары.
 */
@Composable
private fun PairColorsSection(
    pairColorGroup: PairColorGroup,
    onColorChanged: (hex: String, type: PairColorType) -> Unit,
) {
    val pairColors = remember(pairColorGroup) { pairColorGroup.toColor() }
    val pairColorsDefault = PairColorGroup.default().toColor()

    ColorPreference(
        title = stringResource(R.string.lecture_color),
        subtitle = stringResource(R.string.color_summary),
        color = pairColors.lectureColor,
        defaultColor = pairColorsDefault.lectureColor,
        onColorChanged = { onColorChanged(it.toHEX(), PairColorType.Lecture) },
    )
    ColorPreference(
        title = stringResource(R.string.seminar_color),
        subtitle = stringResource(R.string.color_summary),
        color = pairColors.seminarColor,
        defaultColor = pairColorsDefault.seminarColor,
        onColorChanged = { onColorChanged(it.toHEX(), PairColorType.Seminar) },
    )
    ColorPreference(
        title = stringResource(R.string.laboratory_color),
        subtitle = stringResource(R.string.color_summary),
        color = pairColors.laboratoryColor,
        defaultColor = pairColorsDefault.laboratoryColor,
        onColorChanged = { onColorChanged(it.toHEX(), PairColorType.Laboratory) },
    )
    ColorPreference(
        title = stringResource(R.string.subgroup_a_color),
        subtitle = stringResource(R.string.color_summary),
        color = pairColors.subgroupAColor,
        defaultColor = pairColorsDefault.subgroupAColor,
        onColorChanged = { onColorChanged(it.toHEX(), PairColorType.SubgroupA) },
    )
    ColorPreference(
        title = stringResource(R.string.subgroup_b_color),
        subtitle = stringResource(R.string.color_summary),
        color = pairColors.subgroupBColor,
        defaultColor = pairColorsDefault.subgroupBColor,
        onColorChanged = { onColorChanged(it.toHEX(), PairColorType.SubgroupB) },
    )
}
