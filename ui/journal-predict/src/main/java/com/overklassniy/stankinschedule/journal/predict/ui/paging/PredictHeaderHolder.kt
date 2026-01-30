package com.overklassniy.stankinschedule.journal.predict.ui.paging

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import com.overklassniy.stankinschedule.core.ui.theme.Dimen

/**
 * ViewHolder для заголовка раздела дисциплины в списке предсказаний.
 *
 * Использует Compose внутри RecyclerView через [ComposeView] и [AppTheme].
 */
class PredictHeaderHolder(
    composeView: ComposeView,
) : ComposeRecyclerHolder(composeView) {

    /**
     * Привязывает данные заголовка к Compose-содержимому.
     *
     * @param data Модель заголовка с названием дисциплины.
     */
    fun bind(data: PredictAdapter.HeaderItem) {
        composeView.setContent {
            AppTheme {
                Card(
                    shape = RectangleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = data.discipline,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(Dimen.ContentPadding)
                    )
                }
            }
        }
    }
}