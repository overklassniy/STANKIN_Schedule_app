package com.overklassniy.stankinschedule.journal.predict.ui.paging

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.journal.predict.ui.model.PredictMark

/**
 * ViewHolder для элемента ввода оценки.
 *
 * @param onMarkChange Коллбэк изменения значения оценки.
 * @param composeView ComposeView для размещения Composable.
 */
class PredictContentHolder(
    private val onMarkChange: (mark: PredictMark, value: Int) -> Unit,
    composeView: ComposeView,
) : ComposeRecyclerHolder(composeView) {

    /**
     * Привязывает модель элемента и настраивает поле ввода.
     *
     * @param data Элемент контента с моделью [PredictMark].
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Suppress("AssignedValueIsNeverRead")
    fun bind(data: PredictAdapter.ContentItem) {
        composeView.setContent {
            AppTheme {

                var currentValue by remember { mutableIntStateOf(data.mark.value) }

                OutlinedTextField(
                    value = if (currentValue == 0) "" else currentValue.toString(),
                    onValueChange = {
                        val number = when {
                            it.isEmpty() -> 0
                            else -> it.toIntOrNull()
                        }
                        if (number != null) {
                            currentValue = number
                            onMarkChange(data.mark, number)
                        }
                    },
                    isError = currentValue == 0,
                    label = { Text(text = data.mark.type.tag) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = Dimen.ContentPadding)
                )
            }
        }
    }
}