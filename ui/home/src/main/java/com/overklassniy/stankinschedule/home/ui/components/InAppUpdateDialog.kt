package com.overklassniy.stankinschedule.home.ui.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.home.ui.R
import com.overklassniy.stankinschedule.home.ui.data.UpdateState

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
