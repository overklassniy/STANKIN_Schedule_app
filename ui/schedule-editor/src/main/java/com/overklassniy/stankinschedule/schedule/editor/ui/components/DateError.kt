package com.overklassniy.stankinschedule.schedule.editor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.overklassniy.stankinschedule.core.ui.R
import com.overklassniy.stankinschedule.core.ui.theme.Dimen

/**
 * Блок отображения текстовой ошибки для редактора даты.
 *
 * Формирует строку с иконкой ошибки и текстом ошибки, выравнивая содержимое по центру.
 *
 * @param error Текст ошибки, который будет показан пользователю.
 * @param modifier Модификатор Compose для внешнего вида и расположения.
 */
@Composable
fun DateError(
    error: String,
    modifier: Modifier = Modifier
) {
    // Горизонтальный ряд с отступом между иконкой и текстом
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier
    ) {
        // Иконка ошибки с цветом из темы
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        )
    }
}