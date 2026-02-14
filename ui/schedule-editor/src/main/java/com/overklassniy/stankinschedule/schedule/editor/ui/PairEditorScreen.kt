package com.overklassniy.stankinschedule.schedule.editor.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.components.OutlinedSelectField
import com.overklassniy.stankinschedule.core.ui.components.TrackCurrentScreen
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.DateEmptyException
import com.overklassniy.stankinschedule.schedule.core.domain.exceptions.PairIntersectException
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateItem
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.model.Time
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type
import com.overklassniy.stankinschedule.schedule.editor.ui.components.DateChip
import com.overklassniy.stankinschedule.schedule.editor.ui.components.DateEditorRequest
import com.overklassniy.stankinschedule.schedule.editor.ui.components.EditorMode
import com.overklassniy.stankinschedule.schedule.editor.ui.components.EditorToolbar
import com.overklassniy.stankinschedule.schedule.editor.ui.components.PairEditorState
import com.overklassniy.stankinschedule.schedule.editor.ui.components.getOrNull
import com.overklassniy.stankinschedule.schedule.widget.ui.ScheduleWidget
import kotlinx.coroutines.launch
import com.overklassniy.stankinschedule.core.ui.R as R_core

/**
 * Экран редактирования пары расписания.
 *
 * Формирует верхнюю панель, содержимое редактора и диалоги подтверждения/ошибок.
 * Управляет показом нижней шторки для редактирования дат.
 *
 * @param mode Режим редактора: создание или редактирование.
 * @param scheduleId Идентификатор расписания, в котором редактируется пара.
 * @param pairId Идентификатор пары (может быть null при создании).
 * @param onBackClicked Обработчик кнопки «Назад».
 * @param viewModel ViewModel редактора пары.
 * @param modifier Модификатор внешнего вида и расположения.
 */
@Suppress("AssignedValueIsNeverRead")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PairEditorScreen(
    mode: EditorMode,
    scheduleId: Long,
    pairId: Long?,
    onBackClicked: () -> Unit,
    viewModel: PairEditorViewModel,
    modifier: Modifier = Modifier,
) {
    TrackCurrentScreen(screen = "PairEditorScreen")

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    var request by remember { mutableStateOf<DateEditorRequest>(DateEditorRequest.New) }
    var isDeletePair by remember { mutableStateOf(false) }

    val pairState by viewModel.pair.collectAsState()

    val editorState = rememberEditorState(pair = pairState.getOrNull())
    val date by viewModel.date.collectAsState()

    var scheduleError by remember { mutableStateOf<Exception?>(null) }
    LaunchedEffect(Unit) {
        viewModel.scheduleErrors.collect { scheduleError = it }
    }

    LaunchedEffect(scheduleId, pairId, mode) {
        viewModel.loadPair(scheduleId, pairId, mode)
    }

    val context = LocalContext.current
    LaunchedEffect(pairState) {
        when (pairState) {
            is PairEditorState.Complete -> {
                ScheduleWidget.updateWidgetById(context, scheduleId, true)
                onBackClicked()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            EditorToolbar(
                onApplyClicked = {
                    viewModel.applyPair(editorState.toPair(date))
                },
                onDeleteClicked = { isDeletePair = true },
                onBackClicked = onBackClicked,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->

        if (isDeletePair) {
            AlertDialog(
                onDismissRequest = { isDeletePair = false },
                title = {
                    Text(text = stringResource(R.string.editor_removing_pair))
                },
                text = {
                    Text(text = stringResource(R.string.editor_removing_pair_detail))
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.deletePair() }
                    ) {
                        Text(text = stringResource(R.string.editor_delete_pair))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { isDeletePair = false }
                    ) {
                        Text(text = stringResource(R_core.string.cancel))
                    }
                }
            )
        }

        scheduleError?.let {
            AlertDialog(
                onDismissRequest = { scheduleError = null },
                title = { Text(text = stringResource(R_core.string.error)) },
                text = {
                    Text(
                        text = when (it) {
                            is PairIntersectException -> {
                                stringResource(R.string.editor_conflict_pair, it.second)
                            }

                            is DateEmptyException -> {
                                stringResource(R.string.editor_empty_date_error)
                            }

                            else -> it.message ?: it.toString()
                        }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { scheduleError = null }
                    ) {
                        Text(text = stringResource(R_core.string.ok))
                    }
                }
            )
        }

        when (pairState) {
            is PairEditorState.Content -> {
                EditorContent(
                    editorState = editorState,
                    date = date,
                    onDateEdit = { item ->
                        request = DateEditorRequest.Edit(item)
                        showBottomSheet = true
                    },
                    onDateNew = {
                        request = DateEditorRequest.New
                        showBottomSheet = true
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(Dimen.ContentPadding)
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            DateEditorBottomSheet(
                request = request,
                viewModel = viewModel,
                onDismissClicked = {
                    sheetScope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showBottomSheet = false
                    }
                },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}

/**
 * Содержимое формы редактирования.
 *
 * Включает поля названия, преподавателя, аудитории, типа занятия, подгруппы,
 * времени начала/окончания, а также список дат с возможностью добавления/редактирования.
 *
 * @param editorState Локальное состояние формы редактора.
 * @param date Текущее состояние дат пары.
 * @param onDateEdit Обработчик редактирования выбранной даты.
 * @param onDateNew Обработчик добавления новой даты.
 * @param modifier Модификатор внешнего вида и расположения.
 * @param startTimes Список допустимых начальных времен пары.
 * @param endTimes Список допустимых конечных времен пары.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun EditorContent(
    editorState: EditorState,
    date: DateModel,
    onDateEdit: (item: DateItem) -> Unit,
    onDateNew: () -> Unit,
    modifier: Modifier = Modifier,
    startTimes: List<String> = Time.STARTS,
    endTimes: List<String> = Time.ENDS
) {
    val endTimesCalc by remember(editorState.startTime) {
        derivedStateOf {
            endTimes.slice(startTimes.indexOf(editorState.startTime) until endTimes.size)
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier
    ) {

        OutlinedTextField(
            value = editorState.title,
            onValueChange = { editorState.title = it },
            label = { Text(text = stringResource(R.string.editor_title_label)) },
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = editorState.lecturer,
            onValueChange = { editorState.lecturer = it },
            label = { Text(text = stringResource(R.string.editor_lecturer_label)) },
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = editorState.classroom,
            onValueChange = { editorState.classroom = it },
            label = { Text(text = stringResource(R.string.editor_classroom_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = editorState.link,
            onValueChange = { editorState.link = it },
            label = { Text(text = stringResource(R.string.editor_link_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedSelectField(
            value = editorState.type,
            onValueChanged = { editorState.type = it },
            items = listOf(
                Type.LECTURE,
                Type.SEMINAR,
                Type.LABORATORY
            ),
            menuLabel = {
                @StringRes val id = when (it) {
                    Type.LECTURE -> R.string.editor_type_lecture
                    Type.SEMINAR -> R.string.editor_type_seminar
                    Type.LABORATORY -> R.string.editor_type_laboratory
                }
                stringResource(id)
            },
            label = { Text(text = stringResource(R.string.editor_type_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedSelectField(
            value = editorState.subgroup,
            onValueChanged = { editorState.subgroup = it },
            items = listOf(
                Subgroup.COMMON,
                Subgroup.A,
                Subgroup.B
            ),
            menuLabel = {
                @StringRes val id = when (it) {
                    Subgroup.COMMON -> R.string.editor_subgroup_common
                    Subgroup.A -> R.string.editor_subgroup_a
                    Subgroup.B -> R.string.editor_subgroup_b
                }
                stringResource(id)
            },
            label = { Text(text = stringResource(R.string.editor_subgroup_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedSelectField(
                value = editorState.startTime,
                onValueChanged = {
                    editorState.startTime = it

                    val s = startTimes.indexOf(it)
                    val e = endTimes.indexOf(editorState.endTime)

                    if (s - e > 0 && s in endTimes.indices) {
                        editorState.endTime = endTimes[s]
                    }
                },
                items = startTimes,
                menuLabel = { it },
                label = {
                    Text(text = stringResource(R.string.editor_time_start))
                },
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            OutlinedSelectField(
                value = editorState.endTime,
                onValueChanged = { editorState.endTime = it },
                items = endTimesCalc,
                menuLabel = { it },
                label = {
                    Text(text = stringResource(R.string.editor_time_end))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.editor_dates),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                date.forEach { item ->
                    DateChip(
                        item = item,
                        onClicked = { onDateEdit(item) },
                        modifier = Modifier.defaultMinSize(minHeight = 38.dp)
                    )
                }

                AssistChip(
                    onClick = onDateNew,
                    label = {
                        Text(text = stringResource(R.string.editor_new_date))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_date),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 38.dp)
                )
            }
        }
    }

}

class EditorState(
    titleField: MutableState<String>,
    lecturerField: MutableState<String>,
    classroomField: MutableState<String>,
    linkField: MutableState<String>,
    typeField: MutableState<Type>,
    subgroupField: MutableState<Subgroup>,
    startTimeField: MutableState<String>,
    endTimeField: MutableState<String>,
) {
    var title by titleField
    var lecturer by lecturerField
    var classroom by classroomField
    var link by linkField
    var type by typeField
    var subgroup by subgroupField
    var startTime by startTimeField
    var endTime by endTimeField


    fun toPair(date: DateModel): PairModel {
        return PairModel(
            title = title,
            lecturer = lecturer,
            classroom = classroom,
            type = type,
            subgroup = subgroup,
            time = Time(startTime, endTime),
            date = date,
            link = link,
        )
    }
}

@Composable
fun rememberEditorState(
    pair: PairModel?
): EditorState {

    val title = rememberSaveable { mutableStateOf("") }
    val lecturer = rememberSaveable { mutableStateOf("") }
    val classroom = rememberSaveable { mutableStateOf("") }
    val link = rememberSaveable { mutableStateOf("") }

    val type = rememberSaveable(
        stateSaver = Saver(save = { it.tag }, restore = { Type.of(it) })
    ) { mutableStateOf(Type.LECTURE) }

    val subgroup = rememberSaveable(
        stateSaver = Saver(save = { it.tag }, restore = { Subgroup.of(it) })
    ) { mutableStateOf(Subgroup.COMMON) }

    val startTime = rememberSaveable { mutableStateOf(Time.STARTS.first()) }
    val endTime = rememberSaveable { mutableStateOf(Time.ENDS.first()) }

    return remember(pair) {
        EditorState(
            titleField = title,
            lecturerField = lecturer,
            classroomField = classroom,
            linkField = link,
            typeField = type,
            subgroupField = subgroup,
            startTimeField = startTime,
            endTimeField = endTime
        ).apply {
            if (pair != null) {
                this.title = pair.title
                this.lecturer = pair.lecturer
                this.classroom = pair.classroom
                this.link = pair.link
                this.type = pair.type
                this.subgroup = pair.subgroup
                this.startTime = pair.time.startString()
                this.endTime = pair.time.endString()
            }
        }
    }
}