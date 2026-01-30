package com.overklassniy.stankinschedule.schedule.table.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.core.ui.components.RadioGroup
import com.overklassniy.stankinschedule.core.ui.components.RadioItem
import com.overklassniy.stankinschedule.schedule.table.ui.R
import com.overklassniy.stankinschedule.core.ui.R as R_core

/**
 * Состояние диалога выбора формата сохранения или отправки.
 *
 * @property onFormatSelected Колбэк выбора формата. Вызывается при подтверждении.
 * @property isShow Признак отображения диалога. true - диалог видим.
 * Инвариант: при закрытии диалога isShow всегда устанавливается в false.
 */
class TableFormatDialogState internal constructor(
    internal val onFormatSelected: (format: ExportFormat) -> Unit,
    internal val isShow: MutableState<Boolean>
) {
    /**
     * Открывает диалог выбора формата.
     *
     * Побочный эффект: меняет isShow на true, что инициирует показ диалога.
     */
    fun showDialog() {
        isShow.value = true
    }
}

/**
 * Создает и запоминает состояние диалога формата.
 *
 * @param onFormatSelected Обработчик выбора формата.
 * @return Экземпляр состояния диалога, привязанный к композиции.
 */
@Composable
fun rememberFormatDialogState(
    onFormatSelected: (format: ExportFormat) -> Unit,
): TableFormatDialogState {
    val isShow = remember { mutableStateOf(false) }
    return remember(onFormatSelected) {
        TableFormatDialogState(onFormatSelected, isShow)
    }
}

/**
 * Диалог выбора формата экспорта.
 *
 * Формирует UI: AlertDialog с заголовком и радиокнопками выбора формата.
 *
 * @param title Заголовок диалога.
 * @param state Состояние диалога и обработчик результата.
 * @param modifier Модификатор внешнего вида и расположения.
 * @return Ничего не возвращает. Изменяет состояние через state.
 */
@Suppress("AssignedValueIsNeverRead")
@Composable
fun TableFormatDialog(
    title: String,
    state: TableFormatDialogState,
    modifier: Modifier = Modifier
) {
    if (state.isShow.value) {
        var currentFormat by remember { mutableStateOf(ExportFormat.Image) }

        AlertDialog(
            title = { Text(text = title) },
            text = {
                RadioGroup(
                    title = stringResource(R.string.choose_format)
                ) {
                    // Варианты выбора формата. currentFormat хранит промежуточный выбор.
                    RadioItem(
                        title = stringResource(R.string.format_image),
                        selected = currentFormat == ExportFormat.Image,
                        onClick = { currentFormat = ExportFormat.Image },
                    )
                    RadioItem(
                        title = stringResource(R.string.format_pdf),
                        selected = currentFormat == ExportFormat.Pdf,
                        onClick = { currentFormat = ExportFormat.Pdf },
                    )
                }
            },
            onDismissRequest = { state.isShow.value = false },
            dismissButton = {
                TextButton(
                    onClick = { state.isShow.value = false }
                ) {
                    Text(text = stringResource(R_core.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { state.onFormatSelected(currentFormat); state.isShow.value = false }
                ) {
                    Text(text = stringResource(R_core.string.ok))
                }
            },
            modifier = modifier
        )
    }
}