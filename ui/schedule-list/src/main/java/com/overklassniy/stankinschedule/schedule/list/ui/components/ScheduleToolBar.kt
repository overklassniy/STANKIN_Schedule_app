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
 * Верхняя панель списка расписаний в обычном режиме.
 *
 * Отображает заголовок экрана и кнопку перехода в режим действий.
 *
 * @param onActionMode Обработчик включения режима действий.
 * @param modifier Модификатор внешнего вида и расположения.
 * @param title Заголовок панели.
 * @param scrollBehavior Поведение прокрутки AppBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleToolBar(
    onActionMode: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.schedule_list_title),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(
                onClick = onActionMode
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}