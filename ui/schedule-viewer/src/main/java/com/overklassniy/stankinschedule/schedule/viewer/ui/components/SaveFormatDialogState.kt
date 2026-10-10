package com.overklassniy.stankinschedule.schedule.viewer.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Состояние диалога выбора формата сохранения.
 *
 * @property onFormatSelected Колбэк, вызываемый при подтверждении выбора.
 * @property isShow Признак отображения диалога. true — диалог открыт.
 * Инвариант: при закрытии диалога isShow становится false.
 */
class SaveFormatDialogState internal constructor(
    internal val onFormatSelected: (format: ExportFormat) -> Unit,
    internal val isShow: MutableState<Boolean>
) {
    /**
     * Открывает диалог выбора формата.
     * Побочный эффект: меняет isShow на true.
     */
    fun showDialog() {
        isShow.value = true
    }
}

/**
 * Создает и запоминает состояние диалога формата.
 *
 * @param onFormatSelected Обработчик выбора формата.
 * @return Состояние диалога, привязанное к композиции.
 */
@Composable
fun rememberSaveFormatDialogState(
    onFormatSelected: (format: ExportFormat) -> Unit,
): SaveFormatDialogState {
    val isShow = remember { mutableStateOf(false) }
    return remember(onFormatSelected) {
        SaveFormatDialogState(onFormatSelected, isShow)
    }
}
