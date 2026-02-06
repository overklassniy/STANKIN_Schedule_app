package com.overklassniy.stankinschedule.schedule.repository.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BackdropScaffold
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SnackbarDuration
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.components.Stateful
import com.overklassniy.stankinschedule.core.ui.components.TrackCurrentScreen
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.schedule.parser.ui.ScheduleParserActivity
import com.overklassniy.stankinschedule.schedule.repository.ui.components.BackLayerContent
import com.overklassniy.stankinschedule.schedule.repository.ui.components.ChooseNameDialog
import com.overklassniy.stankinschedule.schedule.repository.ui.components.DownloadEvent
import com.overklassniy.stankinschedule.schedule.repository.ui.components.DownloadState
import com.overklassniy.stankinschedule.schedule.repository.ui.components.FrontLayerContent
import com.overklassniy.stankinschedule.schedule.repository.ui.components.RepositoryError
import com.overklassniy.stankinschedule.schedule.repository.ui.components.RepositoryLoading
import com.overklassniy.stankinschedule.schedule.repository.ui.components.RepositoryToolBar
import com.overklassniy.stankinschedule.schedule.repository.ui.components.RequiredNameDialog
import kotlinx.coroutines.launch

/**
 * Экран репозитория расписаний.
 *
 * Управляет поиском, фильтрами, загрузкой и именованием расписаний.
 *
 * @param onBackPressed Обработчик навигации назад.
 * @param viewModel ViewModel экрана репозитория.
 * @param modifier Модификатор внешнего вида и расположения.
 */
@Suppress("AssignedValueIsNeverRead")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleRepositoryScreen(
    onBackPressed: () -> Unit,
    viewModel: ScheduleRepositoryViewModel,
    modifier: Modifier = Modifier,
) {
    TrackCurrentScreen(screen = "ScheduleRepositoryScreen")

    val context = LocalContext.current
    val scaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)

    val description by viewModel.description.collectAsState()
    val repositoryItems by viewModel.repositoryItems.collectAsState()
    val download = viewModel.download.collectAsState(initial = null)

    var isRequiredName by remember { mutableStateOf<DownloadState.RequiredName?>(null) }
    var isChooseName by remember { mutableStateOf<DownloadState.RequiredName?>(null) }

    val downloadFailedMessage = stringResource(R.string.repository_download_failed)

    val scope = rememberCoroutineScope()
    BackHandler(scaffoldState.isRevealed) {
        scope.launch { scaffoldState.conceal() }
    }

    // Наблюдаем за состоянием загрузки файла из ViewModel
    val fileDownloadState by viewModel.fileDownload.collectAsState()

    // Диалог загрузки
    if (fileDownloadState is FileDownloadState.Loading) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(
                            R.string.repository_start_download,
                            (fileDownloadState as FileDownloadState.Loading).scheduleName
                        )
                    )
                }
            }
        )
    }

    // Обработка успешной загрузки — открываем визард ScheduleParserActivity
    LaunchedEffect(fileDownloadState) {
        when (val state = fileDownloadState) {
            is FileDownloadState.Success -> {
                val intent = ScheduleParserActivity.createIntent(
                    context, state.filePath, state.scheduleName
                )
                context.startActivity(intent)
                viewModel.clearFileDownload()
            }

            is FileDownloadState.Failed -> {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = downloadFailedMessage,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearFileDownload()
            }

            else -> {}
        }
    }

    // Обработка RequiredName из SharedFlow
    LaunchedEffect(download.value) {
        when (val state = download.value) {
            is DownloadState.RequiredName -> {
                isRequiredName = state
            }

            else -> {}
        }
    }

    isRequiredName?.let { state ->
        RequiredNameDialog(
            scheduleName = state.item.name,
            onRename = {
                isRequiredName = null
                isChooseName = state
            },
            onDismiss = { isRequiredName = null }
        )
    }

    isChooseName?.let { state ->
        ChooseNameDialog(
            scheduleName = state.item.name,
            onRename = { name ->
                isChooseName = null
                viewModel.onDownloadEvent(DownloadEvent.StartDownload(name, state.item))
            },
            onDismiss = { isChooseName = null }
        )
    }

    val grade by viewModel.grade.collectAsState()
    val course by viewModel.course.collectAsState()

    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    BackdropScaffold(
        appBar = {
            RepositoryToolBar(
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onSearchToggle = { viewModel.toggleSearch() },
                onFilterClick = {
                    if (scaffoldState.isRevealed) {
                        scope.launch { scaffoldState.conceal() }
                    } else {
                        scope.launch { scaffoldState.reveal() }
                    }
                },
                onRefresh = { viewModel.refresh() },
                onBackPressed = onBackPressed,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        },
        frontLayerBackgroundColor = MaterialTheme.colorScheme.surface,
        frontLayerContentColor = MaterialTheme.colorScheme.onSurface,
        frontLayerScrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.60f),
        backLayerBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        backLayerContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        scaffoldState = scaffoldState,
        backLayerContent = {
            Stateful(
                state = description,
                onSuccess = { _ ->
                    BackLayerContent(
                        selectedGrade = grade,
                        onGradeSelected = { viewModel.updateGrade(it) },
                        selectedCourse = course,
                        onCourseSelected = { viewModel.updateCourse(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                },
                onLoading = {
                    RepositoryLoading(
                        modifier = Modifier
                            .padding(Dimen.ContentPadding)
                            .fillMaxWidth()
                    )
                },
                onFailed = {
                    RepositoryError(
                        error = it,
                        onRetryClicked = { viewModel.reloadDescription() },
                        modifier = Modifier
                            .padding(Dimen.ContentPadding)
                            .fillMaxWidth()
                    )
                }
            )
        },
        frontLayerContent = {
            Stateful(
                state = repositoryItems,
                onSuccess = { data ->
                    FrontLayerContent(
                        repositoryItems = data,
                        onItemClicked = { item ->
                            viewModel.onDownloadEvent(DownloadEvent.StartDownload(item.name, item))
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    )
                },
                onLoading = {
                    RepositoryLoading(
                        modifier = Modifier
                            .padding(Dimen.ContentPadding)
                            .fillMaxSize()
                    )
                },
                onFailed = {
                    RepositoryError(
                        error = it,
                        onRetryClicked = { viewModel.reloadCategory() },
                        modifier = Modifier
                            .padding(Dimen.ContentPadding)
                            .fillMaxSize()
                    )
                }
            )
        },
        modifier = modifier
    )
}