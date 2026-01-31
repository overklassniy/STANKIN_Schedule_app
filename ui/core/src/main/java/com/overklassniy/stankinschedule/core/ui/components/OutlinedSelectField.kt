package com.overklassniy.stankinschedule.core.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

/**
 * Поле выбора со стилем Outlined, использующее ExposedDropdownMenuBox.
 *
 * @param value Текущее выбранное значение.
 * @param onValueChanged Коллбэк изменения значения.
 * @param items Список доступных значений.
 * @param menuLabel Функция, возвращающая текст для значения.
 * @param modifier Модификатор поля.
 * @param label Заголовок поля.
 * @param prefix Префиксный контент.
 * @param suffix Суффиксный контент.
 * @param supportingText Вспомогательный текст.
 */
@Suppress("AssignedValueIsNeverRead")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OutlinedSelectField(
    value: T,
    onValueChanged: (value: T) -> Unit,
    items: List<T>,
    menuLabel: @Composable (item: T) -> String,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null
) {

    var isExposed by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExposed,
        onExpandedChange = { isExposed = it },
    ) {
        OutlinedTextField(
            value = menuLabel(value),
            onValueChange = {},
            readOnly = true,
            label = label,
            singleLine = true,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExposed) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
        )

        ExposedDropdownMenu(
            expanded = isExposed,
            onDismissRequest = { isExposed = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(text = menuLabel(item))
                    },
                    onClick = {
                        onValueChanged(item)
                        isExposed = false
                    },
                )
            }
        }
    }
}