package com.overklassniy.stankinschedule.schedule.creator.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.overklassniy.stankinschedule.schedule.creator.ui.R
import kotlinx.coroutines.delay
import com.overklassniy.stankinschedule.core.ui.R as R_core

/**
 * Диалог создания нового расписания.
 *
 * Формирует поле ввода имени, сообщения об ошибках и кнопки Create/Cancel.
 *
 * @param state Состояние процесса создания.
 * @param onDismiss Закрыть диалог.
 * @param onCreate Колбэк создания расписания с указанным именем.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.create_schedule))
        },
        text = {
            val focusRequester = remember { FocusRequester() }
            val keyboardController = LocalSoftwareKeyboardController.current

            // Реакция на изменение состояния: подготовка UI и закрытие диалога при успехе
            LaunchedEffect(state) {
                showExistError = state is CreateState.AlreadyExist
                showCreateError = state is CreateState.Error

                if (state is CreateState.New) {
                    currentValue = ""

                    // Небольшая задержка перед запросом фокуса, чтобы диалог успел построиться
                    delay(timeMillis = 300)
                    focusRequester.requestFocus()
                }
                if (state is CreateState.Success) {
                    onDismiss()
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                OutlinedTextField(
                    value = currentValue,
                    onValueChange = {
                        currentValue = it
                        // Сбрасываем ошибки при новом вводе
                        showExistError = false
                        showCreateError = false
                    },
                    singleLine = true,
                    isError = showExistError || showCreateError,
                    label = { Text(text = stringResource(R.string.new_schedule_name)) },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            // Показываем клавиатуру при фокусе на поле ввода
                            if (it.isFocused) {
                                keyboardController?.show()
                            }
                        }
                )

                AnimatedVisibility(visible = showExistError) {
                    Text(
                        text = stringResource(R.string.schedule_exists),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                AnimatedVisibility(visible = showCreateError) {
                    Text(
                        text = stringResource(R.string.schedule_create_error),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Разрешаем создание только при непустом имени
                    if (currentValue.isNotEmpty()) {
                        onCreate(currentValue.trim())
                    }
                },
                // Блокируем кнопку при пустом имени или наличии конфликта
                enabled = currentValue.isNotEmpty() && !showExistError
            ) {
                Text(
                    text = stringResource(R.string.create)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R_core.string.cancel)
                )
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}