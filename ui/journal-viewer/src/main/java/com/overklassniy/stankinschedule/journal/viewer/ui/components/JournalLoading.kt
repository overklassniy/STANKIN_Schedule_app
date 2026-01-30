package com.overklassniy.stankinschedule.journal.viewer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Индикатор загрузки для экрана журнала.
 *
 * Отображает круговой прогресс, центрированный внутри контейнера.
 *
 * @param modifier Модификатор для внешнего оформления.
 */
@Composable
fun JournalLoading(
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