package com.overklassniy.stankinschedule.schedule.list.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.components.TrackCurrentScreen
import com.overklassniy.stankinschedule.core.ui.ext.Zero
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.list.ui.components.ScheduleActionItem
import com.overklassniy.stankinschedule.schedule.list.ui.components.ScheduleActionToolbar
import com.overklassniy.stankinschedule.schedule.list.ui.components.ScheduleItem
import com.overklassniy.stankinschedule.schedule.list.ui.components.ScheduleRemoveDialog
import com.overklassniy.stankinschedule.schedule.list.ui.components.ScheduleToolBar
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ScheduleScreen(
    onScheduleCreate: () -> Unit,
    onScheduleClicked: (scheduleId: Long) -> Unit,
    viewModel: ScheduleScreenViewModel,
    modifier: Modifier = Modifier,
) {
    TrackCurrentScreen(screen = "ScheduleScreen")

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val editableMode by viewModel.editableMode.collectAsState()

    BackHandler(enabled = editableMode) {
        viewModel.setEditable(false)
    }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            viewModel.schedulesMove(from.index, to.index)
        },
    )
    val schedules by viewModel.schedules.collectAsState()
    val favorite by viewModel.favorite.collectAsState()

    var isScheduleRemove by remember { mutableStateOf<ScheduleInfo?>(null) }
    var isSelectedRemove by remember { mutableStateOf<Int?>(null) }

    isScheduleRemove?.let { scheduleToRemove ->
        ScheduleRemoveDialog(
            text = stringResource(R.string.schedule_single_remove, scheduleToRemove.scheduleName),
            onRemove = {
                viewModel.removeSchedule(scheduleToRemove)
                isScheduleRemove = null
            },
            onDismiss = {
                isScheduleRemove = null
            }
        )
    }

    isSelectedRemove?.let { countToRemove ->
        ScheduleRemoveDialog(
            text = stringResource(R.string.schedule_selected_remove, countToRemove),
            onRemove = {
                viewModel.removeSelectedSchedules()
                isSelectedRemove = null
            },
            onDismiss = {
                isSelectedRemove = null
            }
        )
    }

    Scaffold(
        topBar = {
            if (editableMode) {
                ScheduleActionToolbar(
                    selectedCount = viewModel.selected.count { it.value },
                    onActionClose = {
                        viewModel.setEditable(false)
                    },
                    onRemoveSelected = {
                        isSelectedRemove = it
                    },
                    scrollBehavior = scrollBehavior
                )
            } else {
                ScheduleToolBar(
                    onActionMode = { viewModel.setEditable(true) },
                    scrollBehavior = scrollBehavior
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !reorderState.listState.isScrollInProgress && !editableMode,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = onScheduleCreate
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.Zero,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = reorderState.listState,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(bottom = 72.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .reorderable(reorderState)
            ) {
                schedules?.let { data ->
                    // Если нет расписаний
                    if (data.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.no_schedules),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(Dimen.ContentPadding)
                            )
                        }
                    }

                    // Список с расписаниями
                    items(data, key = { it.id }) { schedule ->
                        if (editableMode) {
                            ReorderableItem(
                                reorderableState = reorderState,
                                key = schedule.id
                            ) {
                                ScheduleActionItem(
                                    schedule = schedule,
                                    isSelected = viewModel.isSelected(schedule.id),
                                    onClicked = {
                                        viewModel.selectSchedule(schedule.id)
                                    },
                                    reorderedState = reorderState,
                                    modifier = Modifier.fillParentMaxWidth()
                                )
                            }
                        } else {
                            ScheduleItem(
                                schedule = schedule,
                                isFavorite = favorite == schedule.id,
                                onClicked = {
                                    onScheduleClicked(schedule.id)
                                },
                                onLongClicked = {
                                    viewModel.setEditable(true)
                                    viewModel.selectSchedule(schedule.id)
                                },
                                onScheduleFavorite = {
                                    viewModel.setFavorite(schedule.id)
                                },
                                onScheduleRemove = {
                                    isScheduleRemove = schedule
                                },
                                modifier = Modifier.fillParentMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}