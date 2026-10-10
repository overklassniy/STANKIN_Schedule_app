package com.overklassniy.stankinschedule.core.ui.components

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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.overklassniy.stankinschedule.core.ui.R

/**
 * Диалог ввода имени с полем текста, индикацией ошибок и кнопками подтверждения/отмены.
 *
 * Используется для создания и переименования расписаний.
 *
 * @param title Заголовок диалога.
 * @param label Подпись поля ввода.
 * @param value Текущее значение поля ввода.
 * @param onValueChange Колбэк изменения значения поля.
 * @param showExistError Флаг отображения ошибки существования имени.
 * @param existErrorText Текст ошибки существования имени.
 * @param showError Флаг отображения ошибки операции.
 * @param errorText Текст ошибки операции.
 * @param confirmText Текст кнопки подтверждения.
 * @param focusRequester Запросчик фокуса для поля ввода.
 * @param showKeyboardOnFocus Показывать клавиатуру при получении фокуса полем.
 * @param onConfirm Колбэк подтверждения с очищенным от пробелов значением.
 * @param onDismiss Колбэк закрытия диалога.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NameInputDialog(
    title: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    showExistError: Boolean,
    existErrorText: String,
    showError: Boolean,
    errorText: String,
    confirmText: String,
    focusRequester: FocusRequester,
    showKeyboardOnFocus: Boolean = false,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    isError = showExistError || showError,
                    label = { Text(text = label) },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (showKeyboardOnFocus && it.isFocused) {
                                keyboardController?.show()
                            }
                        }
                )

                AnimatedVisibility(visible = showExistError) {
                    Text(
                        text = existErrorText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                AnimatedVisibility(visible = showError) {
                    Text(
                        text = errorText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (value.isNotEmpty()) {
                        onConfirm(value.trim())
                    }
                },
                enabled = value.isNotEmpty() && !showExistError
            ) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}
