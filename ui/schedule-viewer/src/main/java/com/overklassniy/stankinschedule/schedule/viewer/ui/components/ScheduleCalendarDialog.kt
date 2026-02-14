package com.overklassniy.stankinschedule.schedule.viewer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.overklassniy.stankinschedule.core.ui.R
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type
import com.overklassniy.stankinschedule.schedule.core.ui.PairColors
import org.joda.time.LocalDate

/**
 * Данные о парах дня для отображения в календаре.
 *
 * @property pairTypes Список типов пар в этот день (для отрисовки точек).
 * @property isDayOff Признак выходного дня по данным isdayoff.ru.
 */
data class CalendarDayData(
    val pairTypes: List<Type> = emptyList(),
    val isDayOff: Boolean = false,
)

/**
 * Диалог календаря расписания с точками пар и пометкой выходных дней.
 *
 * @param selectedDate Текущая выбранная дата.
 * @param dayDataMap Карта дней месяца: LocalDate -> CalendarDayData.
 * @param pairColors Цвета типов пар.
 * @param onDateSelected Коллбэк при выборе даты.
 * @param onMonthChanged Коллбэк при смене месяца (year, month).
 * @param onDismissRequest Коллбэк закрытия диалога.
 */
@Composable
fun ScheduleCalendarDialog(
    selectedDate: LocalDate,
    dayDataMap: Map<LocalDate, CalendarDayData>,
    pairColors: PairColors,
    onDateSelected: (date: LocalDate) -> Unit,
    onMonthChanged: (year: Int, month: Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var displayedYear by remember { mutableIntStateOf(selectedDate.year) }
    var displayedMonth by remember { mutableIntStateOf(selectedDate.monthOfYear) }
    var pickedDate by remember { mutableStateOf(selectedDate) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { onDateSelected(pickedDate) }
            ) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = {
            MonthNavigationHeader(
                year = displayedYear,
                month = displayedMonth,
                onPreviousMonth = {
                    if (displayedMonth == 1) {
                        displayedMonth = 12
                        displayedYear -= 1
                    } else {
                        displayedMonth -= 1
                    }
                    onMonthChanged(displayedYear, displayedMonth)
                },
                onNextMonth = {
                    if (displayedMonth == 12) {
                        displayedMonth = 1
                        displayedYear += 1
                    } else {
                        displayedMonth += 1
                    }
                    onMonthChanged(displayedYear, displayedMonth)
                }
            )
        },
        text = {
            CalendarGrid(
                year = displayedYear,
                month = displayedMonth,
                selectedDate = pickedDate,
                dayDataMap = dayDataMap,
                pairColors = pairColors,
                onDayClicked = { date -> pickedDate = date }
            )
        }
    )
}

/**
 * Заголовок с навигацией по месяцам.
 */
@Composable
private fun MonthNavigationHeader(
    year: Int,
    month: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    val monthDate = LocalDate(year, month, 1)
    // Joda-Time: MMMM = полное название месяца (не LLLL как в SimpleDateFormat)
    val monthName = monthDate.toString("MMMM yyyy")
        .replaceFirstChar { it.uppercase() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = onPreviousMonth) {
            Text(
                text = "◀",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = monthName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onNextMonth) {
            Text(
                text = "▶",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Сетка дней месяца с точками пар и пометкой выходных.
 */
@Composable
private fun CalendarGrid(
    year: Int,
    month: Int,
    selectedDate: LocalDate,
    dayDataMap: Map<LocalDate, CalendarDayData>,
    pairColors: PairColors,
    onDayClicked: (LocalDate) -> Unit,
) {
    val firstDayOfMonth = LocalDate(year, month, 1)
    val daysInMonth = firstDayOfMonth.dayOfMonth().maximumValue
    // Joda: Monday=1 … Sunday=7
    val startDayOfWeek = firstDayOfMonth.dayOfWeek

    // Сокращённые названия дней недели (Пн, Вт, ...)
    val dayHeaders = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    Column {
        // Заголовки дней недели
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dayHeaders.forEach { header ->
                Text(
                    text = header,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Сетка дней
        val totalCells = startDayOfWeek - 1 + daysInMonth
        val rows = (totalCells + 6) / 7

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height((rows * 52).dp),
            userScrollEnabled = false
        ) {
            // Пустые ячейки до начала месяца
            items(startDayOfWeek - 1) {
                Box(modifier = Modifier.size(40.dp, 48.dp))
            }

            // Дни месяца
            items(daysInMonth) { index ->
                val day = index + 1
                val date = LocalDate(year, month, day)
                val dayData = dayDataMap[date]
                val hasPairs = dayData != null && dayData.pairTypes.isNotEmpty()
                val isDayOff = dayData?.isDayOff == true && !hasPairs
                val isSelected = date == selectedDate
                val isToday = date == LocalDate.now()

                CalendarDayCell(
                    day = day,
                    isSelected = isSelected,
                    isToday = isToday,
                    isDimmed = isDayOff,
                    pairTypes = dayData?.pairTypes ?: emptyList(),
                    pairColors = pairColors,
                    onClick = { onDayClicked(date) }
                )
            }
        }
    }
}

/**
 * Ячейка одного дня в календаре.
 */
@Composable
private fun CalendarDayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    isDimmed: Boolean,
    pairTypes: List<Type>,
    pairColors: PairColors,
    onClick: () -> Unit,
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    val alpha = if (isDimmed) 0.4f else 1f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .alpha(alpha)
            .size(40.dp, 48.dp)
            .clip(CircleShape)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
        ) {
            Text(
                text = day.toString(),
                fontSize = 14.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }

        // Точки пар (максимум 5 видимых точек)
        if (pairTypes.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(top = 1.dp)
            ) {
                pairTypes.take(5).forEach { type ->
                    val dotColor = when (type) {
                        Type.LECTURE -> pairColors.lectureColor
                        Type.SEMINAR -> pairColors.seminarColor
                        Type.LABORATORY -> pairColors.laboratoryColor
                    }
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(color = dotColor, shape = CircleShape)
                    )
                }
            }
        }
    }
}