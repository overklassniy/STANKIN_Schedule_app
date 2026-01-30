package com.overklassniy.stankinschedule.home.ui.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.overklassniy.stankinschedule.core.domain.ext.subHours
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.home.ui.R
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
 * @param currentLastUpdate Поставщик времени последнего обновления.
 * @return [InAppUpdateState] для отображения и управления процессом.
 */
@Composable
fun rememberInAppUpdater(
    saveLastUpdate: (last: DateTime) -> Unit,
    currentLastUpdate: () -> DateTime?
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

/**
 * Диалог обновления приложения: отображает доступность обновления и прогресс.
 *
 * @param state Состояние In‑App Update.
 * @param modifier Модификатор.
 */
@Composable
fun InAppUpdateDialog(
    state: InAppUpdateState,
    modifier: Modifier = Modifier
) {
    val progress = state.progress.value
    if (progress != null && progress !is UpdateState.UpToDate) {
        ElevatedCard(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.ContentPadding)
            ) {
                UpdateContent(
                    progress = progress,
                    onLater = state::later,
                    onUpdate = state::startUpdate,
                    onRestart = state::restart
                )
            }
        }
    }
}

/**
 * Контент диалога в зависимости от состояния обновления.
 *
 * @param progress Текущее состояние.
 * @param onLater Отложить обновление.
 * @param onUpdate Запустить обновление.
 * @param onRestart Перезапустить приложение после загрузки.
 */
@Composable
private fun UpdateContent(
    progress: UpdateState,
    onLater: () -> Unit,
    onUpdate: (info: AppUpdateInfo) -> Unit,
    onRestart: () -> Unit,
) {
    when (progress) {
        is UpdateState.UpdateRequired -> {
            UpdateRequiredContent(
                onLater = onLater,
                onUpdate = { onUpdate(progress.info) }
            )
        }

        is UpdateState.UpdateProgress -> {
            UpdateProgressContent(
                progress = progress
            )
        }

        is UpdateState.UpdateRestart -> {
            UpdateRestartContent(
                onRestart = onRestart
            )
        }
    }
}

/**
 * Секция диалога при доступности обновления.
 *
 * @param onLater Отложить обновление.
 * @param onUpdate Запустить процесс обновления.
 */
@Composable
private fun UpdateRequiredContent(
    onLater: () -> Unit,
    onUpdate: () -> Unit
) {
    Text(text = stringResource(R.string.update_available))

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(onClick = onLater) {
            Text(text = stringResource(R.string.later))
        }
        TextButton(onClick = onUpdate) {
            Text(text = stringResource(R.string.update))
        }
    }
}

/**
 * Секция диалога с индикатором прогресса загрузки.
 *
 * @param progress Состояние прогресса.
 */
@Composable
private fun UpdateProgressContent(
    progress: UpdateState.UpdateProgress
) {
    Text(
        text = stringResource(R.string.updating),
        modifier = Modifier.padding(bottom = Dimen.ContentPadding)
    )

    if (!progress.progress.isFinite() || progress.progress == 0f) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimen.ContentPadding)
        )
    } else {
        LinearProgressIndicator(
            progress = { progress.progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimen.ContentPadding)
        )
    }
}

/**
 * Секция диалога для завершённого обновления с предложением перезапуска.
 *
 * @param onRestart Действие по перезапуску.
 */
@Composable
private fun UpdateRestartContent(
    onRestart: () -> Unit,
) {
    Text(text = stringResource(R.string.update_restart))

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(onClick = onRestart) {
            Text(text = stringResource(R.string.restart))
        }
    }
}