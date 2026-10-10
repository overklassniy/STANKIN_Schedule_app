package com.overklassniy.stankinschedule.home.ui.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.overklassniy.stankinschedule.core.domain.ext.subHours
import com.overklassniy.stankinschedule.home.ui.data.GooglePlayInAppUpdater
import com.overklassniy.stankinschedule.home.ui.data.InAppUpdater
import com.overklassniy.stankinschedule.home.ui.data.UpdateState
import org.joda.time.DateTime

/**
 * Состояние In‑App Update: хранит прогресс и операции управления обновлением.
 *
 * @property progress Текущее состояние обновления [UpdateState].
 */
class InAppUpdateState internal constructor(
    private val updateManager: InAppUpdater,
    private val updateLauncher: (info: AppUpdateInfo) -> Unit,
    internal val progress: State<UpdateState?>
) {
    internal fun later() {
        updateManager.later()
    }

    internal fun startUpdate(info: AppUpdateInfo) {
        updateLauncher(info)
    }

    internal fun restart() {
        updateManager.completeUpdate()
    }
}

/**
 * Создаёт и запоминает состояние In‑App Update.
 *
 * Проводит проверку обновления и обновляет дату последнего обновления.
 *
 * @param saveLastUpdate Коллбэк сохранения времени последнего успешного обновления.
 * @param currentLastUpdate Suspend-поставщик времени последнего обновления.
 * @return [InAppUpdateState] для отображения и управления процессом.
 */
@Composable
fun rememberInAppUpdater(
    saveLastUpdate: (last: DateTime) -> Unit,
    currentLastUpdate: suspend () -> DateTime?
): InAppUpdateState {

    val context = LocalContext.current
    val updateManager: InAppUpdater = remember { GooglePlayInAppUpdater(context) }
    val progress = updateManager.updateState.collectAsState()

    val updateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_CANCELED) {
                updateManager.later()
            }
        }
    )

    LaunchedEffect(progress.value) {
        if (progress.value is UpdateState.UpToDate) {
            saveLastUpdate(DateTime.now())
        }
    }

    LaunchedEffect(currentLastUpdate) {
        val lastUpdate = currentLastUpdate()
        if (lastUpdate == null || lastUpdate subHours DateTime.now() > 24 * 7) {
            updateManager.checkUpdate()
        }
    }

    DisposableEffect(Unit) {
        onDispose { updateManager.onDestroy() }
    }

    return remember(
        saveLastUpdate,
        currentLastUpdate
    ) {
        InAppUpdateState(
            updateManager = updateManager,
            updateLauncher = { info ->
                updateManager.startUpdate(
                    info = info,
                    launcher = updateLauncher,
                    options = AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE)
                )
            },
            progress = progress
        )
    }
}
