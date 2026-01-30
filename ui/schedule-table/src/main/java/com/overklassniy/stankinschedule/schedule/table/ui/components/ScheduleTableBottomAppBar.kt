package com.overklassniy.stankinschedule.schedule.table.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.overklassniy.stankinschedule.schedule.table.domain.model.TableMode
import com.overklassniy.stankinschedule.schedule.table.ui.R
import org.joda.time.LocalDate

/**
 * Нижняя панель управления таблицей расписания.
 *
 * Формирует UI: BottomAppBar с кнопками назад и вперед, селектором режима
 * отображения и кнопкой настроек. При режиме Weekly отображает номер недели.
 *
 * @param tableMode Текущий режим представления таблицы. Влияет на доступность стрелок.
 * @param onTableModeChanged Обработчик изменения режима таблицы.
 * @param page Номер страницы для недельного режима. Влияет на подпись недели.
 * @param onBackClicked Обработчик клика по кнопке назад.
 * @param onNextClicked Обработчик клика по кнопке вперед.
 * @param onSettingsClicked Обработчик клика по кнопке настроек.
 * @param modifier Модификатор внешнего вида и расположения.
 * @return Ничего не возвращает. Побочных эффектов нет.
 */
@Composable
fun ScheduleTableBottomAppBar(
    tableMode: TableMode,
    onTableModeChanged: (mode: TableMode) -> Unit,
    page: Int,
    onBackClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier
    ) {
        // Стрелки доступны только в недельном режиме. derivedStateOf оптимизирует пересчеты.
        val isArrowsEnabled by remember(tableMode) {
            derivedStateOf { tableMode == TableMode.Weekly }
        }

        IconButton(
            onClick = onBackClicked,
            enabled = isArrowsEnabled
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = null
            )
        }

        Box {
            var showTableModeList by remember { mutableStateOf(false) }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showTableModeList = true
                    }
            ) {
                Text(
                    text = if (isArrowsEnabled) {
                        stringResource(R.string.table_weekly)
                    } else {
                        stringResource(R.string.table_full)
                    },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )

                if (isArrowsEnabled) {
                    Text(
                        text = getDateWeek(page),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            DropdownMenu(
                expanded = showTableModeList,
                onDismissRequest = { showTableModeList = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.table_full)) },
                    onClick = { onTableModeChanged(TableMode.Full); showTableModeList = false }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.table_weekly)) },
                    onClick = { onTableModeChanged(TableMode.Weekly); showTableModeList = false }
                )
            }
        }

        IconButton(
            onClick = onNextClicked,
            enabled = isArrowsEnabled
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_next),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onSettingsClicked) {
            Icon(
                painter = painterResource(R.drawable.ic_table_settings),
                contentDescription = null
            )
        }
    }
}


/**
 * Возвращает строку диапазона дат текущей недели с учетом смещения страницы.
 *
 * Алгоритм: вычисляет дату на сегодня с прибавлением 7 * page дней, затем
 * формирует диапазон от понедельника до воскресенья в формате dd.MM.yyyy.
 *
 * @param page Номер страницы. Каждая единица равна 7 дням смещения относительно текущей даты.
 * Допустимые значения: целые числа, отрицательные для прошлых недель.
 * @return Строка вида dd.MM.yyyy-dd.MM.yyyy.
 */
private fun getDateWeek(page: Int): String {
    val date = LocalDate.now().plusDays(page * 7)
    return date.withDayOfWeek(1).toString("dd.MM.yyyy") +
            "-" +
            date.withDayOfWeek(7).toString("dd.MM.yyyy")
}