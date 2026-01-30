package com.overklassniy.stankinschedule.schedule.editor.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.overklassniy.stankinschedule.schedule.editor.ui.R
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

/**
 * Поле ввода даты с иконкой календаря.
 *
 * Формирует OutlinedTextField с плейсхолдером и кнопкой открытия календаря. При нажатии
 * на иконку календаря пытается распарсить введённую дату, иначе берёт текущую дату.
 *
 * @param value Текущее текстовое значение даты.
 * @param onValueChange Обработчик изменения текста.
 * @param onCalendarClicked Колбэк открытия календаря, получает вычисленную дату.
 * @param label Компонент метки поля.
 * @param modifier Модификатор внешнего вида и расположения.
 * @param dateFormat Формат даты для парсинга, по умолчанию "dd.MM.yyyy".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedDateField(
    value: String,
    onValueChange: (value: String) -> Unit,
    onCalendarClicked: (date: LocalDate) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dateFormat: String = "dd.MM.yyyy",
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        singleLine = true,
        placeholder = {
            Text(text = "dd.mm.yyyy")
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    // Пытаемся распарсить введённую дату по заданному формату
                    val current = try {
                        DateTimeFormat.forPattern(dateFormat).parseLocalDate(value)
                    } catch (_: Exception) {
                        // Если парсинг не удался, подставляем текущую дату
                        LocalDate.now()
                    }
                    onCalendarClicked(current)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar_picker),
                    contentDescription = null
                )
            }
        },
        modifier = modifier
    )
}