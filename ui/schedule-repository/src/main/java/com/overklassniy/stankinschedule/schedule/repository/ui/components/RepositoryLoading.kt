package com.overklassniy.stankinschedule.schedule.repository.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Индикатор загрузки данных репозитория.
 *
 * Показывает круговой индикатор по центру контейнера.
 *
 * @param modifier Модификатор внешнего вида и расположения.
 */
@Composable
fun RepositoryLoading(
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