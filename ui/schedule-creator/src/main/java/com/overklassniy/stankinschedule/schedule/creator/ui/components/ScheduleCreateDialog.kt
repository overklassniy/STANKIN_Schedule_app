package com.overklassniy.stankinschedule.schedule.creator.ui.components

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
import com.overklassniy.stankinschedule.schedule.creator.ui.R
import kotlinx.coroutines.delay

/** Задержка перед запросом фокуса, чтобы диалог успел построиться. */
private const val FOCUS_DELAY_MS = 300L

/**
 * Диалог создания нового расписания.
 *
 * Формирует поле ввода имени, сообщения об ошибках и кнопки Create/Cancel.
 *
 * @param state Состояние процесса создания.
 * @param onDismiss Закрыть диалог.
 * @param onCreate Колбэк создания расписания с указанным именем.
 */
@Composable
fun ScheduleCreateDialog(
    state: CreateState,
    onDismiss: () -> Unit,
    onCreate: (scheduleName: String) -> Unit,
) {
    // Флаги отображения ошибок: существование расписания и ошибка создания
    var showExistError by remember { mutableStateOf(false) }
    var showCreateError by remember { mutableStateOf(false) }

    // Текущее значение имени расписания, сохраняется при изменениях конфигурации
    var currentValue by rememberSaveable { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    // Реакция на изменение состояния: подготовка UI и закрытие диалога при успехе
    LaunchedEffect(state) {
        showExistError = state is CreateState.AlreadyExist
        showCreateError = state is CreateState.Error

        if (state is CreateState.New) {
            currentValue = ""

            // Небольшая задержка перед запросом фокуса, чтобы диалог успел построиться
            delay(timeMillis = FOCUS_DELAY_MS)
            focusRequester.requestFocus()
        }
        if (state is CreateState.Success) {
            onDismiss()
        }
    }

    NameInputDialog(
        title = stringResource(R.string.create_schedule),
        label = stringResource(R.string.new_schedule_name),
        value = currentValue,
        onValueChange = {
            currentValue = it
            // Сбрасываем ошибки при новом вводе
            showExistError = false
            showCreateError = false
        },
        showExistError = showExistError,
        existErrorText = stringResource(R.string.schedule_exists),
        showError = showCreateError,
        errorText = stringResource(R.string.schedule_create_error),
        confirmText = stringResource(R.string.create),
        focusRequester = focusRequester,
        showKeyboardOnFocus = true,
        onConfirm = onCreate,
        onDismiss = onDismiss,
    )
}
