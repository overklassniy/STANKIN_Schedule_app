package com.overklassniy.stankinschedule.schedule.editor.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.core.ui.components.BackButton
import com.overklassniy.stankinschedule.schedule.editor.ui.R

/**
 * Верхняя панель редактора пары.
 *
 * Формирует заголовок, кнопку назад, а также действия удаления и применения изменений.
 *
 * @param onApplyClicked Обработчик нажатия на кнопку применения.
 * @param onDeleteClicked Обработчик нажатия на кнопку удаления пары.
 * @param onBackClicked Обработчик кнопки навигации назад.
 * @param scrollBehavior Поведение прокрутки AppBar, может быть null.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorToolbar(
    onApplyClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onBackClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.editor_title))
        },
        // Кнопка навигации назад
        navigationIcon = {
            BackButton(onClick = onBackClicked)
        },
        // Кнопки действий редактора: удалить и применить
        actions = {
            IconButton(onClick = onDeleteClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete_pair),
                    contentDescription = null
                )
            }
            IconButton(onClick = onApplyClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_done),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}