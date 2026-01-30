package com.overklassniy.stankinschedule.schedule.creator.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.schedule.creator.ui.R
import com.overklassniy.stankinschedule.core.ui.R as R_core

/**
 * Диалог об отказе в разрешении на чтение файлов.
 *
 * Показывает пояснение и кнопку OK.
 *
 * @param onDismiss Колбэк закрытия диалога.
 */
@Composable
fun ReadPermissionDeniedDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        // Заголовок диалога
        title = {
            Text(text = stringResource(R.string.read_permission_denied))
        },
        // Основной текст с пояснением причины отказа
        text = {
            Text(text = stringResource(R.string.read_permissions_details))
        },
        // Единственная кнопка подтверждения закрывает диалог
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R_core.string.ok))
            }
        },
        // Закрытие по вне-диалоговым событиям делегируется наружу
        onDismissRequest = onDismiss,
    )
}