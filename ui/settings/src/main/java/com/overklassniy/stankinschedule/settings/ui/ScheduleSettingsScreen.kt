package com.overklassniy.stankinschedule.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.settings.ui.components.SettingsScaffold
import com.overklassniy.stankinschedule.settings.ui.components.SwitchPreference

/**
 * Экран настроек расписания.
 *
 * Содержит только переключатель вертикального режима просмотра.
 * Настройка цветов пар перенесена на экран темы оформления (ThemeSettingsScreen).
 *
 * @param viewModel ViewModel с состоянием настроек расписания.
 * @param onBackPressed Обработчик нажатия кнопки «Назад».
 * @param modifier Модификатор для внешнего контейнера.
 */
@Composable
fun ScheduleSettingsScreen(
    viewModel: SettingsViewModel,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsScaffold(
        title = stringResource(R.string.setting_schedule_title),
        onBackPressed = onBackPressed,
        modifier = modifier
    ) {
        val isVertical by viewModel.isVerticalViewer.collectAsState(false)

        SwitchPreference(
            title = stringResource(R.string.schedule_view_ver),
            subtitle = stringResource(R.string.schedule_view_ver_summary),
            checked = isVertical,
            onCheckedChange = viewModel::setVerticalViewer
        )
    }
}
