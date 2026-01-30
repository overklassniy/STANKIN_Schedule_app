package com.overklassniy.stankinschedule.schedule.viewer.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.overklassniy.stankinschedule.core.ui.components.BackButton
import com.overklassniy.stankinschedule.schedule.viewer.ui.R
import com.overklassniy.stankinschedule.core.ui.R as R_core

/**
 * Верхняя панель экрана просмотра расписания.
 *
 * Формирует UI: TopAppBar с заголовком, кнопкой назад, кнопкой выбора даты и меню действий.
 *
 * @param scheduleName Имя расписания. Влияет на заголовок.
 * @param onBackClicked Обработчик клика по кнопке назад.
 * @param onDayChangeClicked Обработчик открытия выбора даты.
 * @param onAddClicked Обработчик добавления пары.
 * @param onRemoveSchedule Обработчик удаления расписания.
 * @param onRenameSchedule Обработчик переименования расписания.
 * @param onSaveToDevice Обработчик сохранения расписания на устройство.
 * @param modifier Модификатор внешнего вида и расположения.
 * @param scrollBehavior Поведение прокрутки панели.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleViewerToolBar(
    scheduleName: String,
    onBackClicked: () -> Unit,
    onDayChangeClicked: () -> Unit,
    onAddClicked: () -> Unit,
    onRemoveSchedule: () -> Unit,
    onRenameSchedule: () -> Unit,
    onSaveToDevice: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = scheduleName,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        navigationIcon = {
            BackButton(onClick = onBackClicked)
        },
        actions = {
            IconButton(
                onClick = onDayChangeClicked
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_date_picker),
                    contentDescription = null
                )
            }

            // Переключение видимости меню действий.
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    painter = painterResource(R_core.drawable.ic_action_more),
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.schedule_add_pair))
                    },
                    onClick = {
                        onAddClicked()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.save_to_device))
                    },
                    onClick = {
                        onSaveToDevice()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.rename_schedule))
                    },
                    onClick = {
                        onRenameSchedule()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.remove_schedule))
                    },
                    onClick = {
                        onRemoveSchedule()
                        showMenu = false
                    }
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}