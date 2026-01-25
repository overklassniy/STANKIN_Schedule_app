package com.overklassniy.stankinschedule.schedule.table.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.overklassniy.stankinschedule.core.ui.components.BackButton
import com.overklassniy.stankinschedule.schedule.table.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTableAppBar(
    scheduleName: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(R.string.table_view_title),
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                AnimatedVisibility(
                    visible = scheduleName.isNotEmpty()
                ) {
                    Text(
                        text = scheduleName,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        },
        navigationIcon = {
            BackButton(onClick = onBackClicked)
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}