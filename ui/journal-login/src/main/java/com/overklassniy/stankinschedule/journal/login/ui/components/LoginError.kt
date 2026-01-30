package com.overklassniy.stankinschedule.journal.login.ui.components

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
import com.overklassniy.stankinschedule.core.ui.components.LocalAnalytics
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.core.ui.utils.exceptionDescription
import com.overklassniy.stankinschedule.journal.login.ui.R

/**
 * Отображает ошибку входа: иконка и локализованное описание.
 *
 * @param error Исключение, полученное при попытке входа.
 * @param modifier Модификатор.
 */
@Composable
fun LoginError(
    error: Throwable,
    modifier: Modifier = Modifier,
) {
    val analytics = LocalAnalytics.current

    // Записываем исключение в аналитику
    analytics.recordException(error)

    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_login_error),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = exceptionDescription(error),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        )
    }
}