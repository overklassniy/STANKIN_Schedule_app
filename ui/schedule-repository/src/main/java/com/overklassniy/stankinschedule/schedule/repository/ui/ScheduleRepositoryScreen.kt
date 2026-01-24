package com.overklassniy.stankinschedule.schedule.repository.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.overklassniy.stankinschedule.core.ui.components.Stateful
import com.overklassniy.stankinschedule.core.ui.components.TrackCurrentScreen
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.schedule.parser.ui.ScheduleParserActivity
import com.overklassniy.stankinschedule.schedule.repository.ui.components.*
import com.overklassniy.stankinschedule.schedule.repository.ui.worker.ScheduleDownloadWorker
import kotlinx.coroutines.launch

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
    var currentWorkerName by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    BackHandler(scaffoldState.isRevealed) {
        scope.launch { scaffoldState.conceal() }
    }

    currentWorkerName?.let { workerName ->
        val workManager = WorkManager.getInstance(context)
        val workInfoLiveData = workManager.getWorkInfosForUniqueWorkLiveData(workerName)
        val workInfos by workInfoLiveData.observeAsState(initial = emptyList())
        
        LaunchedEffect(workInfos) {
            val workInfo = workInfos.firstOrNull()
            when (workInfo?.state) {
                WorkInfo.State.SUCCEEDED -> {
                    val filePath = workInfo.outputData.getString(ScheduleDownloadWorker.OUTPUT_FILE_PATH)
                    val scheduleName = workInfo.outputData.getString(ScheduleDownloadWorker.OUTPUT_SCHEDULE_NAME)
                    
                    if (filePath != null && scheduleName != null) {
                        val intent = ScheduleParserActivity.createIntent(context, filePath, scheduleName)
                        context.startActivity(intent)
                        currentWorkerName = null
                    }
                }
                WorkInfo.State.FAILED -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.getString(R.string.repository_download_failed),
                        duration = SnackbarDuration.Short
                    )
                    currentWorkerName = null
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(download.value) {
        when (val state = download.value) {
            is DownloadState.StartDownload -> {
                val workerName = ScheduleDownloadWorker.startWorker(
                    context = context,
                    scheduleName = state.scheduleName,
                    item = state.item,
                    downloadOnly = true
                )
                currentWorkerName = workerName
                scaffoldState.snackbarHostState.showSnackbar(
                    message = context.getString(
                        R.string.repository_start_download, state.scheduleName
                    ),
                    duration = SnackbarDuration.Short
                )
            }
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

    val category by viewModel.category.collectAsState()
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
                onSuccess = { data ->
                    BackLayerContent(
                        selectedCategory = category,
                        scheduleCategories = data.categories,
                        onCategorySelected = { viewModel.updateCategory(it) },
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
