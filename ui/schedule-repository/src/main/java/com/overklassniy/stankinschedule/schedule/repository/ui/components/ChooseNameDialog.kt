package com.overklassniy.stankinschedule.schedule.repository.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.schedule.repository.ui.R
import com.overklassniy.stankinschedule.core.ui.R as R_core

@Suppress("AssignedValueIsNeverRead")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseNameDialog(
    scheduleName: String,
    onRename: (scheduleName: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentValue by remember { mutableStateOf(scheduleName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.repository_choose_name))
        },
        text = {
            OutlinedTextField(
                value = currentValue,
                onValueChange = { currentValue = it },
                isError = currentValue.isEmpty(),
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (currentValue.isNotEmpty()) {
                        onRename(currentValue)
                    }
                },
                enabled = currentValue.isNotEmpty()
            ) {
                Text(text = stringResource(R_core.string.ok))
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