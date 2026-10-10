package com.overklassniy.stankinschedule.core.ui.components

import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.overklassniy.stankinschedule.core.ui.R
import androidx.core.net.toUri

/**
 * Отображает диалоги объяснения и отказа для разрешения записи.
 *
 * @param state Состояние [FileSaveState].
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FileSaveDialogs(
    state: FileSaveState
) {
    if (state.showRationaleDialog.value) {
        AlertDialog(
            title = { Text(text = stringResource(R.string.permission_write_title)) },
            text = { Text(text = stringResource(R.string.permission_write_text)) },
            onDismissRequest = { state.showRationaleDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.showRationaleDialog.value = false
                        state.permission.launchPermissionRequest()
                    }
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        )
    }

    if (state.showDeniedDialog.value) {
        val context = LocalContext.current
        AlertDialog(
            title = { Text(text = stringResource(R.string.permission_write_denied_title)) },
            text = { Text(text = stringResource(R.string.permission_write_denied_text)) },
            onDismissRequest = { state.showDeniedDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = ("package:" + context.packageName).toUri()
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            addCategory(Intent.CATEGORY_DEFAULT)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = stringResource(R.string.permission_write_to_settings))
                }
            }
        )
    }
}
