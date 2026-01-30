package com.overklassniy.stankinschedule.schedule.list.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.schedule.list.ui.R

/**
 * Верхняя панель в режиме действий.
 *
 * Показывает количество выбранных элементов, кнопку закрытия режима и действие удаления выбранных.
 *
 * @param selectedCount Количество выбранных расписаний.
 * @param onActionClose Обработчик выхода из режима действий.
 * @param onRemoveSelected Обработчик удаления выбранных расписаний.
 * @param modifier Модификатор внешнего вида и расположения.
 * @param scrollBehavior Поведение прокрутки AppBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleActionToolbar(
    selectedCount: Int,
    onActionClose: () -> Unit,
    onRemoveSelected: (selected: Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.schedule_count_selected, selectedCount),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onActionClose
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_action_close),
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onRemoveSelected(selectedCount) },
                enabled = selectedCount > 0
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_clear_all),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}