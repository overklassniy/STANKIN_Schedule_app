package com.overklassniy.stankinschedule.core.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

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
