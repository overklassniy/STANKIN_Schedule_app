package com.overklassniy.stankinschedule.journal.viewer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.journal.core.domain.model.Student
import com.overklassniy.stankinschedule.journal.viewer.ui.R


/**
 * Карточка информации о студенте.
 *
 * Отображает имя и группу студента, а также текущий и предсказанный рейтинг.
 * Значения рейтинга могут быть отсутствовать — в этом случае выводится «--».
 *
 * @param student Модель студента.
 * @param rating Текущий рейтинг студента (строка или null).
 * @param predictRating Предсказанный рейтинг (строка или null).
 * @param modifier Модификатор для внешнего оформления.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInfo(
    student: Student,
    rating: String?,
    predictRating: String?,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.ContentPadding)
        ) {
            Text(
                text = student.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = student.group,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.predict_rating, predictRating ?: "--"),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.current_rating, rating ?: "--"),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}