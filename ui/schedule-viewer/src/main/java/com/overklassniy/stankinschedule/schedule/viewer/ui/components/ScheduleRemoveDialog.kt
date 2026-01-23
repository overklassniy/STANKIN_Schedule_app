package com.overklassniy.stankinschedule.schedule.viewer.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.schedule.viewer.ui.R
import com.overklassniy.stankinschedule.core.ui.R as R_core


@Composable
fun ScheduleRemoveDialog(
    scheduleName: String,
    onRemove: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.schedule_remove_title))
        },
        text = {
            Text(text = stringResource(R.string.schedule_single_remove, scheduleName))
        },
        confirmButton = {
            TextButton(
                onClick = onRemove
            ) {
                Text(text = stringResource(R.string.schedule_remove))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R_core.string.cancel))
            }
        }
    )
}