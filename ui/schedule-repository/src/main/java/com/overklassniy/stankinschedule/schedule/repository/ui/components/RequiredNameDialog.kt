package com.overklassniy.stankinschedule.schedule.repository.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.schedule.repository.ui.R
import com.overklassniy.stankinschedule.core.ui.R as R_core

/**
 * Диалог уведомления о существующем имени расписания.
 *
 * Предлагает переименовать или отменить действие.
 *
 * @param scheduleName Текущее имя расписания.
 * @param onRename Обработчик перехода к переименованию.
 * @param onDismiss Обработчик закрытия диалога.
 */
@Composable
fun RequiredNameDialog(
    scheduleName: String,
    onRename: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.repository_schedule_exist))
        },
        text = {
            Text(text = stringResource(R.string.repository_schedule_exist_text, scheduleName))
        },
        confirmButton = {
            TextButton(
                onClick = onRename
            ) {
                Text(text = stringResource(R.string.repository_rename))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R_core.string.cancel))
            }
        }
    )
}