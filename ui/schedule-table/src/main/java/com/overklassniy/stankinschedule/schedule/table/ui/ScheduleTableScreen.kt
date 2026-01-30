package com.overklassniy.stankinschedule.schedule.table.ui

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.components.FileSaveDialogs
import com.overklassniy.stankinschedule.core.ui.components.TrackCurrentScreen
import com.overklassniy.stankinschedule.core.ui.components.rememberFileSaveState
import com.overklassniy.stankinschedule.core.ui.ext.shareDataIntent
import com.overklassniy.stankinschedule.schedule.table.domain.model.TableMode
import com.overklassniy.stankinschedule.schedule.table.ui.components.ExportProgress
import com.overklassniy.stankinschedule.schedule.table.ui.components.ExportSnackBar
import com.overklassniy.stankinschedule.schedule.table.ui.components.ExportType
import com.overklassniy.stankinschedule.schedule.table.ui.components.ScheduleTableAppBar
import com.overklassniy.stankinschedule.schedule.table.ui.components.ScheduleTableBottomAppBar
import com.overklassniy.stankinschedule.schedule.table.ui.components.SettingsSheet
import com.overklassniy.stankinschedule.schedule.table.ui.components.TableFormatDialog
import com.overklassniy.stankinschedule.schedule.table.ui.components.TableView
import com.overklassniy.stankinschedule.schedule.table.ui.components.ZoomableBox
import com.overklassniy.stankinschedule.schedule.table.ui.components.rememberFormatDialogState
import kotlinx.coroutines.launch


/**
 * Экран просмотра таблицы расписания.
 *
 * Формирует UI: холст с таблицей, панель инструментов сверху и снизу,
 * шторка настроек, диалоги выбора формата и сохранения, индикатор экспорта.
 * Обрабатывает жесты масштабирования и клики через ZoomableBox.
 *
 * @param scheduleId Идентификатор расписания для загрузки.
 * @param viewModel ViewModel экрана. Управляет данными и экспортом.
 * @param onBackClicked Обработчик возврата назад.
 * @param modifier Модификатор внешнего вида и расположения.
 * @return Ничего не возвращает. Содержит побочные эффекты через LaunchedEffect.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ScheduleTableScreen(
    scheduleId: Long,
    viewModel: ScheduleTableViewModel,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    TrackCurrentScreen(screen = "ScheduleTableScreen")

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = rememberTopAppBarState())

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val sheetScope = rememberCoroutineScope()
    // Перехватываем системную кнопку назад для закрытия шторки настроек.
    BackHandler(enabled = sheetState.isVisible) {
        sheetScope.launch { sheetState.hide() }
    }

    val schedule by viewModel.scheduleName.collectAsState()
    LaunchedEffect(scheduleId) {
        viewModel.loadSchedule(scheduleId)
    }

    val tableConfig by viewModel.tableConfig.collectAsState()
    val table by viewModel.table.collectAsState()

    val color = MaterialTheme.colorScheme.onBackground
    val windowInfo = LocalWindowInfo.current
    val longScreenSize by remember(windowInfo) {
        derivedStateOf {
            val size = windowInfo.containerSize
            maxOf(size.width, size.height).toFloat()
        }
    }

    var tableMode by rememberSaveable { mutableStateOf(TableMode.Full) }
    var pageNumber by rememberSaveable { mutableIntStateOf(0) }
    var showUI by rememberSaveable { mutableStateOf(true) }

    // Синхронизируем конфигурацию таблицы с изменениями цвета, размеров экрана и режима.
    LaunchedEffect(color, longScreenSize, tableMode, pageNumber) {
        viewModel.setConfig(
            color = color.toArgb(),
            longScreenSize = longScreenSize,
            mode = tableMode,
            pageNumber = pageNumber
        )
    }

    val sendFormatState = rememberFormatDialogState(
        onFormatSelected = viewModel::sendSchedule
    )

    val saveState = rememberFileSaveState(
        onPickerResult = { uri -> if (uri != null) viewModel.saveSchedule(uri) }
    )
    val saveFormatState = rememberFormatDialogState(
        onFormatSelected = { format ->
            viewModel.setSaveFormat(format)
            saveState.save(
                fileName = schedule.ifEmpty { "null" },
                fileType = format.memeType
            )
        }
    )

    val exportProgress by viewModel.exportProgress.collectAsState()

    val context = LocalContext.current
    // Реакция на завершение экспорта. При типе Send открываем системный диалог отправки.
    LaunchedEffect(exportProgress) {
        val progress = exportProgress

        if (progress is ExportProgress.Finished) {
            if (progress.type == ExportType.Send) {
                val intent = shareDataIntent(progress.path, progress.format.memeType)
                val choose = Intent.createChooser(intent, null)
                context.startActivity(choose)
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            SettingsSheet(
                tableMode = tableMode,
                onTableModeChanged = { tableMode = it },
                onSendCopyClicked = {
                    sendFormatState.showDialog()
                    sheetScope.launch { sheetState.hide() }
                },
                onSaveClicked = {
                    saveFormatState.showDialog()
                    sheetScope.launch { sheetState.hide() }
                },
                modifier = Modifier.navigationBarsPadding()
            )
        },
    ) {
        TableFormatDialog(
            title = stringResource(R.string.send_copy),
            state = sendFormatState
        )

        TableFormatDialog(
            title = stringResource(R.string.save_as),
            state = saveFormatState
        )

        FileSaveDialogs(
            state = saveState
        )

        Box(
            modifier = modifier
        ) {
            ZoomableBox(
                minScale = 1f,
                maxScale = 6f,
                onTap = { showUI = !showUI },
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) { scale, offsetX, offsetY ->

                LaunchedEffect(scale) {
                    scrollBehavior.state.contentOffset = if (scale > 1f) -100f else 0f
                }

                with(table) {
                    if (this != null) {
                        TableView(
                            table = this,
                            tableConfig = tableConfig,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offsetX,
                                    translationY = offsetY,
                                ),
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showUI,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
            ) {
                ScheduleTableAppBar(
                    scheduleName = schedule,
                    onBackClicked = onBackClicked,
                    scrollBehavior = scrollBehavior
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
            ) {
                AnimatedVisibility(
                    visible = exportProgress !is ExportProgress.Nothing,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ExportSnackBar(
                        progress = exportProgress,
                        onOpen = { progress ->
                            val intent = Intent().apply {
                                action = Intent.ACTION_VIEW
                                setDataAndType(progress.path, progress.format.memeType)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        },
                        onCancelJob = viewModel::cancelExport,
                        onClose = viewModel::exportFinished,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }

                AnimatedVisibility(
                    visible = showUI,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ScheduleTableBottomAppBar(
                        tableMode = tableMode,
                        onTableModeChanged = { tableMode = it },
                        page = pageNumber,
                        onBackClicked = { --pageNumber },
                        onNextClicked = { ++pageNumber },
                        onSettingsClicked = { sheetScope.launch { sheetState.show() } }
                    )
                }
            }
        }
    }
}
