package com.overklassniy.stankinschedule.core.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.overklassniy.stankinschedule.core.ui.R


/**
 * Состояние диалога сохранения файла с проверкой разрешения записи.
 *
 * Управляет запросом разрешения, показом диалогов объяснения/отказа и запуском системного пикера.
 */
/**
 * Состояние диалога сохранения файла с проверкой разрешения записи.
 *
 * Управляет запросом разрешения, показом диалогов объяснения/отказа и запуском системного пикера.
 */
@OptIn(ExperimentalPermissionsApi::class)
class FileSaveState internal constructor(
    private val launchPicker: () -> Unit,
    private val launchData: MutableState<String>,
    internal val permission: PermissionState,
    internal val showDeniedDialog: MutableState<Boolean>,
    internal val showRationaleDialog: MutableState<Boolean>
) {
    /**
     * Запускает процесс сохранения файла.
     *
     * @param fileName Имя файла (без расширения).
     * @param fileType MIME-тип (например, "application/pdf").
     */
    /**
     * Запускает процесс сохранения файла.
     *
     * @param fileName Имя файла (без расширения).
     * @param fileType MIME-тип (например, "application/pdf").
     */
    fun save(fileName: String, fileType: String) {
        launchData.value = "$fileName;$fileType"
        when {
            isGranted() -> launchPicker()
            permission.status.shouldShowRationale -> showRationaleDialog.value = true
            else -> permission.launchPermissionRequest()
        }
    }

    /**
     * Проверяет, выдано ли разрешение на запись.
     *
     * Для Android Q (API 29) и выше разрешение считается выданным по умолчанию.
     *
     * @return true, если разрешение есть или версия ОС ≥ Q.
     */
    private fun isGranted(): Boolean {
        return permission.status.isGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }
}

/**
 * Контракт создания документа через системный файловый пикер.
 *
 * Используется для выбора пути и типа создаваемого файла.
 */
private class CreateDocument : ActivityResultContracts.CreateDocument("*/*") {
    /**
     * Формирует Intent для пикера документов.
     *
     * @param context Контекст.
     * @param input Строка формата "fileName;mimeType".
     * @return Сконфигурированный [Intent] для создания файла.
     */
    override fun createIntent(context: Context, input: String): Intent {
        // Разбиваем входные данные на имя и MIME-тип
        val (startPath, type) = input.split(";")
        return super.createIntent(context, startPath)
            .setType(type)
    }
}

/**
 * Создает и запоминает [FileSaveState] для управления сохранением файла.
 *
 * @param onPickerResult Коллбэк с URI результата из системного пикера.
 * @return Состояние [FileSaveState].
 */
/**
 * Создает и запоминает [FileSaveState] для управления сохранением файла.
 *
 * @param onPickerResult Коллбэк с URI результата из системного пикера.
 * @return Состояние [FileSaveState].
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberFileSaveState(
    onPickerResult: (uri: Uri?) -> Unit
): FileSaveState {

    val writeScheduleLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument(),
        onResult = onPickerResult
    )

    val launchData = remember { mutableStateOf("") }
    val launchPicker = { writeScheduleLauncher.launch(launchData.value) }
    val showDeniedDialog = remember { mutableStateOf(false) }
    val showRationaleDialog = remember { mutableStateOf(false) }

    val writePermission = rememberPermissionState(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
        onPermissionResult = { isGranted ->
            if (isGranted) launchPicker() else showDeniedDialog.value = true
        }
    )

    return remember(launchPicker) {
        FileSaveState(
            launchPicker = launchPicker,
            launchData = launchData,
            permission = writePermission,
            showDeniedDialog = showDeniedDialog,
            showRationaleDialog = showRationaleDialog
        )
    }
}

/**
 * Отображает диалоги объяснения и отказа для разрешения записи.
 *
 * @param state Состояние [FileSaveState].
 */
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
                            data = Uri.parse("package:" + context.packageName)
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