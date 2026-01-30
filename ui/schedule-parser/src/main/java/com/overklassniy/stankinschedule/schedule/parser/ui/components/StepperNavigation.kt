package com.overklassniy.stankinschedule.schedule.parser.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.schedule.parser.ui.R
import com.overklassniy.stankinschedule.schedule.parser.ui.model.ParserState

/**
 * Панель навигации по шагам импорта.
 *
 * Содержит кнопки «Назад», «Далее» и «Готово», учитывая текущее состояние.
 *
 * @param parserState Текущее состояние шагов.
 * @param navigateBack Обработчик перехода назад.
 * @param navigateNext Обработчик перехода на следующий шаг.
 * @param navigateDone Обработчик завершения процесса.
 * @param modifier Модификатор внешнего вида и расположения.
 */
@Composable
fun StepperNavigation(
    parserState: ParserState,
    navigateBack: () -> Unit,
    navigateNext: () -> Unit,
    navigateDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (parserState !is ParserState.ImportFinish) {
            OutlinedButton(
                onClick = navigateBack,
                enabled = parserState !is ParserState.SelectFile
            ) {
                Text(text = stringResource(R.string.step_back))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = if (parserState is ParserState.ImportFinish) {
                navigateDone
            } else {
                navigateNext
            },
            enabled = parserState.isSuccess,
        ) {
            Text(
                text = if (parserState is ParserState.ImportFinish) {
                    stringResource(R.string.step_done)
                } else {
                    stringResource(R.string.step_next)
                }
            )
        }
    }
}