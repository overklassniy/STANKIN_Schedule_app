package com.overklassniy.stankinschedule.news.review.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Индикатор вкладок для TabRow.
 *
 * Рисует закруглённую полоску шириной, определяемой layout-модификатором.
 *
 * @param modifier Модификатор для позиционирования.
 * @param color Цвет индикатора.
 */
@Suppress("unused")
@Composable
fun AppTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Box(
        modifier = modifier
            .height(3.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(
                    topStart = 3.dp,
                    topEnd = 3.dp
                )
            ),
        contentAlignment = Alignment.Center,
        content = {}
    )
}