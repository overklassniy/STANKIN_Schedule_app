package com.overklassniy.stankinschedule.schedule.repository.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.core.ui.components.LocalAnalytics
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.core.ui.utils.exceptionDescription
import com.overklassniy.stankinschedule.schedule.repository.ui.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryError(
    error: Throwable,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val analytics = LocalAnalytics.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier
    ) {
        Text(
            text = exceptionDescription(error).let { description ->
                if (description == null) {
                    analytics.recordException(error)
                    error.toString()
                } else {
                    description
                }
            },
        )
        OutlinedCard(
            onClick = onRetryClicked
        ) {
            Text(
                text = stringResource(R.string.repository_retry)
            )
        }
    }
}