package com.overklassniy.stankinschedule.schedule.viewer.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.core.ui.components.NameInputDialog
import com.overklassniy.stankinschedule.schedule.viewer.ui.R

/**
 * Диалог переименования расписания.
 *
 * Формирует UI: AlertDialog с текстовым полем, индикацией ошибок и кнопками.
 *
 * @param currentScheduleName Текущее имя расписания для предварительного заполнения.
 * @param state Состояние диалога и результата переименования.
 * @param onDismiss Обработчик закрытия диалога.
 * @param onRename Обработчик подтверждения нового имени.
 */
@Composable
fun ScheduleRenameDialog(
    currentScheduleName: String,
    state: RenameState,
    onDismiss: () -> Unit,
    onRename: (scheduleName: String) -> Unit,
) {
    var showExistError by remember { mutableStateOf(false) }
    var showCreateError by remember { mutableStateOf(false) }

    var currentValue by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Реакция на изменение состояния диалога.
    // Переключает флаги ошибок и устанавливает фокус при входе в режим Rename.
    LaunchedEffect(state) {
        showExistError = state is RenameState.AlreadyExist
        showCreateError = state is RenameState.Error

        if (state is RenameState.Rename) {
            currentValue = currentScheduleName

            focusRequester.requestFocus()
        }
        if (state is RenameState.Success) {
            onDismiss()
        }
    }

    NameInputDialog(
        title = stringResource(R.string.rename_schedule_title),
        label = stringResource(R.string.current_schedule_name),
        value = currentValue,
        onValueChange = {
            currentValue = it
            showExistError = false
            showCreateError = false
        },
        showExistError = showExistError,
        existErrorText = stringResource(R.string.schedule_exists),
        showError = showCreateError,
        errorText = stringResource(R.string.schedule_rename_error),
        confirmText = stringResource(R.string.rename),
        focusRequester = focusRequester,
        onConfirm = onRename,
        onDismiss = onDismiss,
    )
}
