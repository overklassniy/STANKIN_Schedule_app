package com.overklassniy.stankinschedule.news.review.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Индикатор загрузки новостей.
 *
 * Отображает круговой прогресс в центре контейнера.
 *
 * @param modifier Модификатор для внешнего оформления.
 */
@Composable
fun NewsLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}