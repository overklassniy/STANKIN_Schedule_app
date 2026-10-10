package com.overklassniy.stankinschedule.schedule.table.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

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
