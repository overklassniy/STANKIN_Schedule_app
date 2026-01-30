package com.overklassniy.stankinschedule.journal.predict.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.components.AppScaffold
import com.overklassniy.stankinschedule.core.ui.components.BackButton
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.journal.predict.ui.PredictViewModel
import com.overklassniy.stankinschedule.journal.predict.ui.R
import com.overklassniy.stankinschedule.journal.predict.ui.model.PredictMark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Экран предсказания рейтинга: выбор семестра, ввод предполагаемых оценок и панель результата.
 *
 * @param viewModel ViewModel экрана предсказания.
 * @param modifier Модификатор корневого контейнера.
 * @param onBackPressed Действие «назад».
 */
@Suppress("unused")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictScreen(
    viewModel: PredictViewModel,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    val semesters by viewModel.semesters.collectAsState()
    val currentSemester by viewModel.currentSemester.collectAsState()

    val bottomState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    AppScaffold(
        topBar = {
            PredictToolBar(
                subTitle = currentSemester,
                onBackPressed = onBackPressed,
                onTitleClicked = { scope.launch { bottomState.show() } }
            )
        },
        modifier = modifier
    ) { innerPadding ->

        if (bottomState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = { scope.launch { bottomState.hide() } },
                sheetState = bottomState,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                SemesterSelectorBottomSheet(
                    currentSemester = currentSemester,
                    semesters = semesters,
                    onSemesterSelected = { semester ->
                        viewModel.changeSemester(semester)
                        scope.launch { bottomState.hide() }
                    },
                    modifier = Modifier
                        .padding(vertical = Dimen.ContentPadding * 2)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            val predictMarks by viewModel.predictMarks.collectAsState()
            val predictedRating by viewModel.predictedRating.collectAsState()
            val showExposedMarks by viewModel.showExposedMarks.collectAsState()

            PredictDisciplines(
                predictMarks = predictMarks,
                onPredictMarkChanged = { item, value ->
                    viewModel.updatePredictMark(item, value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            PredictRatingPanel(
                predictedRating = predictedRating,
                showExposedMarks = showExposedMarks,
                onChangeSemester = { scope.launch { bottomState.show() } },
                onShowExposedMarks = { viewModel.toggleShowExposedMarks() },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

/**
 * Верхняя панель экрана предсказания с заголовком и подзаголовком семестра.
 *
 * @param subTitle Текущий выбранный семестр.
 * @param onTitleClicked Обработчик клика по заголовку (открыть выбор семестра).
 * @param onBackPressed Действие «назад».
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictToolBar(
    subTitle: String,
    onTitleClicked: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val titleInteractionSource = remember { MutableInteractionSource() }

    TopAppBar(
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .clickable(
                        interactionSource = titleInteractionSource,
                        indication = null,
                        onClick = onTitleClicked
                    )
            ) {
                Text(
                    text = stringResource(R.string.predict_title),
                    style = MaterialTheme.typography.titleLarge
                )
                if (subTitle.isNotEmpty()) {
                    Text(
                        text = subTitle,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        navigationIcon = {
            BackButton(
                onClick = onBackPressed
            )
        },
    )
}

/**
 * Контент листа выбора семестра.
 *
 * @param currentSemester Текущий семестр.
 * @param semesters Список доступных семестров.
 * @param onSemesterSelected Коллбэк выбора семестра.
 * @param modifier Модификатор контейнера.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterSelectorBottomSheet(
    currentSemester: String,
    semesters: List<String>,
    onSemesterSelected: (semester: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {

        Text(
            text = stringResource(R.string.selected_semester, currentSemester),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimen.ContentPadding * 2)
        )

        semesters.forEach { semester ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = semester == currentSemester,
                        onClick = { onSemesterSelected(semester) },
                        role = Role.RadioButton
                    )
                    .padding(Dimen.ContentPadding)
            ) {
                RadioButton(
                    selected = semester == currentSemester,
                    onClick = null
                )
                Text(
                    text = semester,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Список дисциплин с вводом предполагаемых оценок.
 *
 * @param predictMarks Карта «заголовок → список оценок».
 * @param onPredictMarkChanged Коллбэк изменения значения оценки.
 * @param modifier Модификатор списка.
 * @param contentPadding Внутренние отступы списка.
 */
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun PredictDisciplines(
    predictMarks: Map<String, List<PredictMark>>,
    onPredictMarkChanged: (item: PredictMark, value: Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val keyboardController = LocalSoftwareKeyboardController.current


    LazyColumn(
        verticalArrangement = Arrangement.Bottom,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        predictMarks.forEach { (header, data) ->
            stickyHeader {
                Card(
                    shape = RectangleShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = header,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(Dimen.ContentPadding)
                    )
                }
            }
            itemsIndexed(data) { index, item ->
                Row(
                    modifier = Modifier
                ) {

                    val relocationRequester = remember { BringIntoViewRequester() }
                    var focused by remember { mutableStateOf(false) }
                    val imeVisible = WindowInsets.isImeVisible

                    LaunchedEffect(focused) {
                        if (focused) {
                            var done = false
                            while (!done) {
                                if (imeVisible) {
                                    relocationRequester.bringIntoView()
                                    done = true
                                }
                                delay(100L)
                            }
                        }
                    }

                    TextField(
                        value = if (item.value == 0) "" else item.value.toString(),
                        onValueChange = {
                            val value = when {
                                it.isEmpty() -> 0
                                else -> it.toIntOrNull()
                            }
                            if (value != null) {
                                onPredictMarkChanged(item, value)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if (index == predictMarks.size - 1) {
                                ImeAction.Done
                            } else {
                                ImeAction.Next
                            }
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .bringIntoViewRequester(relocationRequester)
                            .onFocusChanged { focused = it.isFocused }
                    )
                }
            }
        }
    }
}

/**
 * Панель итогового рейтинга и действий.
 *
 * @param predictedRating Предсказанный рейтинг.
 * @param showExposedMarks Флаг отображения открытых оценок.
 * @param onChangeSemester Действие смены семестра.
 * @param onShowExposedMarks Переключение видимости оценок.
 * @param modifier Модификатор панели.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictRatingPanel(
    predictedRating: Float,
    showExposedMarks: Boolean,
    onChangeSemester: () -> Unit,
    onShowExposedMarks: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentRating by animateFloatAsState(targetValue = predictedRating)

    Card(
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.ContentPadding)
        ) {
            IconButton(
                onClick = onChangeSemester
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_repeat),
                    contentDescription = null
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.maybe_rating),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = if (currentRating.isFinite() || currentRating > 0f) {
                        "%.2f".format(currentRating)
                    } else {
                        "--.--"
                    },
                    style = MaterialTheme.typography.titleLarge
                )
            }

            IconButton(
                onClick = onShowExposedMarks
            ) {
                Icon(
                    painter = painterResource(
                        if (showExposedMarks) {
                            R.drawable.ic_discipline_visibility
                        } else {
                            R.drawable.ic_discipline_visibility_off
                        }
                    ),
                    contentDescription = null
                )
            }
        }
    }
}