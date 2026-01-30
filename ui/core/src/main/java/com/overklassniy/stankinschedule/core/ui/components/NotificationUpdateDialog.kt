package com.overklassniy.stankinschedule.core.ui.components

import android.Manifest
import android.os.Build
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.overklassniy.stankinschedule.core.ui.R
import com.overklassniy.stankinschedule.core.ui.notification.NotificationUtils
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme

/**
 * Провайдер значений для предпросмотра тёмной/светлой темы.
 */
/**
 * Провайдер значений для предпросмотра тёмной/светлой темы.
 */
class DarkProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(true, false)
}

/**
 * Предпросмотр диалога обновления уведомлений для тёмной/светлой темы.
 *
 * @param dark Флаг тёмной темы из [DarkProvider].
 */
@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun NotificationUpdateDialogPreview(
    @PreviewParameter(DarkProvider::class) dark: Boolean
) {
    val state = rememberNotificationUpdateState(
        isEnabled = true,
        onChanged = {}
    )

    // Debug show
    state._isShow = true

    AppTheme(dark) {
        Box(modifier = Modifier.fillMaxSize()) {
            NotificationUpdateDialog(
                title = "Journal notification",
                content = "Get notification about marks",
                state = state,
            )
        }
    }
}


/**
 * Состояние диалога обновления уведомлений.
 *
 * Хранит текущий статус и управляет изменением разрешений/включением уведомлений.
 */
/**
 * Состояние диалога обновления уведомлений.
 *
 * Хранит текущий статус и управляет изменением разрешений/включением уведомлений.
 */
class NotificationUpdateState internal constructor(
    internal val isEnabled: Boolean,
    private val onChanged: (enable: Boolean) -> Unit,
) {
    internal var _isShow by mutableStateOf(false)

    /**
     * Устанавливает состояние включения уведомлений.
     *
     * @param enable Включить, если true.
     */
    /**
     * Устанавливает состояние включения уведомлений.
     *
     * @param enable Включить, если true.
     */
    fun setEnabled(enable: Boolean) {
        if (isEnabled != enable) {
            onChanged(enable)
        }
    }

    /**
     * Показывает диалог.
     */
    /**
     * Показывает диалог.
     */
    fun showDialog() {
        _isShow = true
    }
}

/**
 * Создает и запоминает [NotificationUpdateState].
 *
 * @param isEnabled Текущее состояние уведомлений.
 * @param onChanged Коллбэк изменения состояния.
 */
/**
 * Создает и запоминает [NotificationUpdateState].
 *
 * @param isEnabled Текущее состояние уведомлений.
 * @param onChanged Коллбэк изменения состояния.
 */
@Composable
fun rememberNotificationUpdateState(
    isEnabled: Boolean,
    onChanged: (enable: Boolean) -> Unit,
): NotificationUpdateState {
    return remember(isEnabled, onChanged) { NotificationUpdateState(isEnabled, onChanged) }
}

/**
 * Диалог обновления настроек уведомлений.
 *
 * Показывает включение/выключение уведомлений, учитывая разрешение POST_NOTIFICATIONS.
 *
 * @param title Заголовок диалога.
 * @param content Описание.
 * @param state Состояние диалога.
 * @param modifier Модификатор.
 */
/**
 * Диалог обновления настроек уведомлений.
 *
 * Показывает включение/выключение уведомлений, учитывая разрешение POST_NOTIFICATIONS.
 *
 * @param title Заголовок диалога.
 * @param content Описание.
 * @param state Состояние диалога.
 * @param modifier Модификатор.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationUpdateDialog(
    title: String,
    content: String,
    state: NotificationUpdateState,
    modifier: Modifier = Modifier
) {
    if (state._isShow) {
        val context = LocalContext.current
        val isNotificationEnabled by remember(state._isShow, state.isEnabled) {
            derivedStateOf { NotificationUtils.isNotificationAllow(context) }
        }

        val notificationPermission = rememberPermissionState(
            permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.POST_NOTIFICATIONS
            } else {
                "" // nothing
            },
            onPermissionResult = { isGranted ->
                if (isGranted) {
                    state.setEnabled(true)
                } else {
                    state._isShow = false
                }
            }
        )

        AlertDialog(
            title = { Text(text = title) },
            text = {
                Column {
                    Text(
                        text = content,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ChooseItem(
                        title = stringResource(R.string.notification_on),
                        selected = state.isEnabled,
                        onClick = {
                            if (isNotificationEnabled) {
                                state.setEnabled(true)
                            } else {
                                notificationPermission.launchPermissionRequest()
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_notifications_on),
                                contentDescription = null
                            )
                        }
                    )

                    ChooseItem(
                        title = stringResource(R.string.notification_off),
                        selected = !state.isEnabled,
                        onClick = { state.setEnabled(false) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_notifications_off),
                                contentDescription = null
                            )
                        }
                    )
                }
            },
            onDismissRequest = { state._isShow = false },
            confirmButton = {
                TextButton(onClick = { state._isShow = false }) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            modifier = modifier,
        )
    }
}

/**
 * Элемент выбора с иконкой и радиокнопкой.
 *
 * @param title Заголовок.
 * @param selected Выбрано ли.
 * @param onClick Обработчик клика.
 * @param icon Иконка.
 * @param modifier Модификатор.
 */
/**
 * Элемент выбора с иконкой и радиокнопкой.
 *
 * @param title Заголовок.
 * @param selected Выбрано ли.
 * @param onClick Обработчик клика.
 * @param icon Иконка.
 * @param modifier Модификатор.
 */
@Composable
private fun ChooseItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
            )
            .padding(start = 8.dp)
    ) {

        icon()

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(weight = 1f),
        )

        RadioButton(
            selected = selected,
            onClick = onClick,
            interactionSource = interactionSource,
        )
    }
}