package com.overklassniy.stankinschedule.schedule.table.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.utils.exceptionDescription
import com.overklassniy.stankinschedule.schedule.table.ui.R
import kotlinx.coroutines.delay
import com.overklassniy.stankinschedule.core.ui.R as R_core


/**
 * Снackbar-индикатор процесса экспорта.
 *
 * Формирует UI: строка состояния с текстом, прогресс-баром и действиями.
 * - При Running отображает линейный индикатор прогресса и кнопку отмены
 * - При Finished для типа Save показывает кнопку открытия сохраненного файла
 * - При Error автоматически скрывается через короткую задержку
 *
 * @param progress текущее состояние экспорта
 * @param onOpen действие открытия сохраненного файла, если экспорт завершен
 * @param onCancelJob колбэк отмены текущей операции экспорта
 * @param onClose колбэк закрытия снackbar после таймаута или вручную
 * @param modifier модификатор компоновки для контейнера Snackbar
 */
@Composable
fun ExportSnackBar(
    progress: ExportProgress,
    onOpen: (progress: ExportProgress.Finished) -> Unit,
    onCancelJob: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Мемоизация флага Running — избавляет от лишних перерасчетов в компоновке
    val isRunning by remember(progress) { derivedStateOf { progress is ExportProgress.Running } }

    // Управление авто-закрытием: скрыть ошибку через 10 секунд, успешный результат через 5 секунд
    LaunchedEffect(progress) {
        if (progress is ExportProgress.Error) {
            delay(10000)
            onClose()
        }
        if (progress is ExportProgress.Finished) {
            delay(5000)
            onClose()
        }
    }

    Snackbar(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (progress) {
                        is ExportProgress.Error -> exceptionDescription(progress.error)
                        is ExportProgress.Finished -> stringResource(R.string.exported)
                        else -> stringResource(R.string.export_schedule)
                    },
                )

                // Прогресс-бар видим только во время Running
                if (isRunning) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }
            }

            // Кнопка отмены доступна только при Running
            if (isRunning) {
                TextButton(onClick = onCancelJob) {
                    Text(
                        text = stringResource(R_core.string.cancel),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
            // Кнопка «Открыть» доступна только для завершенного сохранения
            if (progress is ExportProgress.Finished && progress.type == ExportType.Save) {
                TextButton(
                    onClick = { onOpen(progress) }
                ) {
                    Text(
                        text = stringResource(R.string.save_open),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}