package com.overklassniy.stankinschedule.journal.viewer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.overklassniy.stankinschedule.core.ui.components.LocalAnalytics
import com.overklassniy.stankinschedule.core.ui.utils.exceptionDescription
import com.overklassniy.stankinschedule.journal.viewer.ui.R

/**
 * Компонент отображения ошибки журнала.
 *
 * Показывает текст ошибки и кнопку «Повторить». Также записывает исключение
 * в аналитику для последующей диагностики.
 *
 * @param error Исключение, возникшее при загрузке/обработке данных.
 * @param onRetry Обработчик нажатия на кнопку повторной попытки.
 * @param modifier Модификатор для внешнего оформления.
 */
@Composable
fun JournalError(
    error: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val analytics = LocalAnalytics.current

    // Записываем исключение в аналитику
    analytics.recordException(error)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = exceptionDescription(error),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = onRetry,
        ) {
            Text(
                text = stringResource(R.string.journal_retry)
            )
        }
    }
}