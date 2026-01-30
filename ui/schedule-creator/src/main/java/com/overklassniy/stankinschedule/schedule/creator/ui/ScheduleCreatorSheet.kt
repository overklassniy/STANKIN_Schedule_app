package com.overklassniy.stankinschedule.schedule.creator.ui

import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.overklassniy.stankinschedule.core.ui.components.TrackCurrentScreen
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.schedule.creator.ui.components.CreateEvent
import com.overklassniy.stankinschedule.schedule.creator.ui.components.CreateState
import com.overklassniy.stankinschedule.schedule.creator.ui.components.ImportState
import com.overklassniy.stankinschedule.schedule.creator.ui.components.ReadPermissionDeniedDialog
import com.overklassniy.stankinschedule.schedule.creator.ui.components.ScheduleCreateDialog


/**
 * Лист создания расписания.
 *
 * Формирует сетку действий: создать новое, импорт с устройства, импорт вручную, из репозитория.
 * Управляет разрешениями на чтение и обработкой состояний создания и импорта.
 *
 * @param onNavigateBack Возврат к предыдущему экрану.
 * @param onRepositoryClicked Открыть репозиторий расписаний.
 * @param onImportClicked Открыть экран импорта из буфера/ввода.
 * @param onShowSnackBar Показ сообщения пользователю.
 * @param viewModel ViewModel, управляющая логикой создания/импорта.
 * @param modifier Внешний модификатор.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScheduleCreatorSheet(
    onNavigateBack: () -> Unit,
    onRepositoryClicked: () -> Unit,
    onImportClicked: () -> Unit,
    onShowSnackBar: (message: String) -> Unit,
    viewModel: ScheduleCreatorViewModel,
    modifier: Modifier = Modifier,
) {
    // Трекинг экрана для аналитики
    TrackCurrentScreen(screen = "ScheduleCreatorSheet")

    val createState by viewModel.createState.collectAsState()
    LaunchedEffect(createState) {
        if (createState is CreateState.Success) {
            onNavigateBack()
        }
    }

    createState?.let {
        ScheduleCreateDialog(
            state = it,
            onDismiss = {
                viewModel.onCreateSchedule(CreateEvent.Cancel)
            },
            onCreate = { scheduleName ->
                viewModel.createSchedule(scheduleName)
            }
        )
    }

    val importState by viewModel.importState.collectAsState()
    // Готовим локализованные сообщения для snackbar из состояния импорта
    val successMessage = if (importState is ImportState.Success) stringResource(
        R.string.schedule_added,
        (importState as ImportState.Success).scheduleName
    ) else null
    val errorMessage =
        if (importState is ImportState.Failed) stringResource(R.string.import_error) else null
    // Запускаем побочный эффект при изменении сообщений: показать уведомление и вернуться назад
    LaunchedEffect(successMessage, errorMessage) {
        successMessage?.let {
            onShowSnackBar(it)
            onNavigateBack()
        }
        errorMessage?.let {
            onShowSnackBar(it)
            onNavigateBack()
        }
    }

    var readDeniedDialog by remember { mutableStateOf(false) }
    if (readDeniedDialog) {
        ReadPermissionDeniedDialog(
            onDismiss = { readDeniedDialog = false }
        )
    }

    // Лаунчер системного диалога выбора файла (только JSON)
    val openScheduleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            if (it != null) {
                viewModel.importSchedule(it)
            }
        }
    )

    // Разрешение на чтение внешнего хранилища для SDK ниже 33 (TIRAMISU)
    val readStoragePermission = rememberPermissionState(
        permission = android.Manifest.permission.READ_EXTERNAL_STORAGE,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                openScheduleLauncher.launch(arrayOf("application/json"))
            } else {
                readDeniedDialog = true
            }
        }
    )


    val createItems = listOf(
        ScheduleCreatorItem(
            title = R.string.schedule_create_new,
            icon = R.drawable.ic_schedule_new,
            onItemClicked = { viewModel.onCreateSchedule(CreateEvent.New) }
        ),
        ScheduleCreatorItem(
            title = R.string.schedule_from_device,
            icon = R.drawable.ic_schedule_from_device,
            onItemClicked = {
                if (readStoragePermission.status.isGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    openScheduleLauncher.launch(arrayOf("application/json"))
                } else {
                    readStoragePermission.launchPermissionRequest()
                }
            }
        ),
        ScheduleCreatorItem(
            title = R.string.schedule_import,
            icon = R.drawable.ic_schedule_import,
            onItemClicked = onImportClicked
        ),
        ScheduleCreatorItem(
            title = R.string.schedule_from_repository,
            icon = R.drawable.ic_schedule_from_repo,
            onItemClicked = onRepositoryClicked
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier.padding(top = Dimen.ContentPadding * 2)
    ) {
        Text(
            text = stringResource(R.string.schedule_creator),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        val configuration = LocalConfiguration.current

        LazyVerticalGrid(
            // В альбомной ориентации показываем 4 столбца, в портретной 2
            columns = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridCells.Fixed(4)
            } else {
                GridCells.Fixed(2)
            },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Используем иконку как ключ: она уникальна для каждого действия
            items(createItems, key = { it.icon }) { item ->
                CreateScheduleItem(
                    item = item,
                    modifier = Modifier.padding(Dimen.ContentPadding),
                    contentPadding = PaddingValues(vertical = Dimen.ContentPadding)
                )
            }
        }
    }
}

/**
 * Элемент сетки действий создания расписания.
 *
 * Показывает иконку и подпись; по клику вызывает обработчик действия.
 *
 * @param item Модель элемента (иконка, заголовок, обработчик).
 * @param modifier Внешний модификатор.
 * @param contentPadding Внутренние отступы содержимого.
 * @param iconSize Размер иконки в dp (по умолчанию 64).
 */
@Composable
private fun CreateScheduleItem(
    item: ScheduleCreatorItem,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    iconSize: Dp = 64.dp,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(Dimen.ContentPadding))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = item.onItemClicked
            )
            .padding(contentPadding)
    ) {
        // Иконка элемента; размер по умолчанию 64 dp — компромисс читабельности и компактности
        Icon(
            painter = painterResource(item.icon),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(item.title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/**
 * Модель элемента сетки создателя расписаний.
 *
 * @property title Идентификатор строкового ресурса заголовка.
 * @property icon Идентификатор ресурса иконки.
 * @property onItemClicked Обработчик клика по элементу.
 */
private class ScheduleCreatorItem(
    @param:StringRes val title: Int,
    @param:DrawableRes val icon: Int,
    val onItemClicked: () -> Unit,
)