package com.overklassniy.stankinschedule.schedule.editor.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateItem
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateRange
import com.overklassniy.stankinschedule.schedule.core.domain.model.DateSingle
import com.overklassniy.stankinschedule.schedule.core.domain.model.Frequency
import com.overklassniy.stankinschedule.schedule.editor.ui.R

/**
 * Чип с датой пары.
 *
 * Формирует подпись для одиночной даты или диапазона с частотой и отображает SuggestionChip.
 *
 * @param item Элемент даты: одиночная или диапазон.
 * @param onClicked Обработчик клика по чипу.
 * @param modifier Внешний модификатор.
 * @param dateFormat Формат даты для вывода, по умолчанию dd.MM.yyyy.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateChip(
    item: DateItem,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
    dateFormat: String = "dd.MM.yyyy",
) {
    val label = when (item) {
        is DateSingle -> {
            item.toString(dateFormat)
        }

        is DateRange -> {
            // Выбираем локализованное обозначение частоты для диапазона
            val frequency = when (item.frequency()) {
                Frequency.EVERY -> stringResource(R.string.frequency_every_week_simple)
                Frequency.THROUGHOUT -> stringResource(R.string.frequency_throughout_simple)
                else -> ""
            }

            // Разделитель дат диапазона задаём как пробел, дефис, пробел
            item.toString(dateFormat, " - ") + " " + frequency
        }
    }

    // Используем SuggestionChip для компактного отображения даты и реакции на клик
    SuggestionChip(
        onClick = onClicked,
        label = {
            Text(
                text = label
            )
        },
        modifier = modifier
    )
}