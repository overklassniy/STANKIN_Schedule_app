package com.overklassniy.stankinschedule.news.review.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp

/**
 * Индикатор вкладок для TabRow.
 *
 * Рисует закруглённую полоску под выбранной вкладкой.
 *
 * @param modifier Модификатор для позиционирования и отступов.
 * @param color Цвет индикатора.
 */
@Composable
fun AppTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(
                    topStartPercent = 100,
                    topEndPercent = 100
                )
            )
    )
}

/**
 * Смещение индикатора в зависимости от состояния Pager.
 *
 * Вычисляет ширину и позицию индикатора между вкладками с учётом
 * текущей страницы и доли прокрутки.
 *
 * @param pagerState Состояние Pager.
 * @param pageIndexMapping Отображение индекса страницы на индекс вкладки.
 * @return Модификатор, размещающий индикатор в пределах TabRow.
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerTabIndicatorOffset(
    scope: TabIndicatorScope,
    pagerState: androidx.compose.foundation.pager.PagerState,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier = with(scope) {
    this@pagerTabIndicatorOffset.tabIndicatorLayout { measurable, constraints, tabPositions ->
        if (tabPositions.isEmpty()) {
            layout(constraints.maxWidth, 0) {}
        } else {
            val currentPage = minOf(tabPositions.lastIndex, pageIndexMapping(pagerState.currentPage))
            val currentTab = tabPositions[currentPage]
            val previousTab = tabPositions.getOrNull(currentPage - 1)
            val nextTab = tabPositions.getOrNull(currentPage + 1)
            val fraction = pagerState.currentPageOffsetFraction

            val indicatorWidth = if (fraction > 0 && nextTab != null) {
                lerp(currentTab.width, nextTab.width, fraction).roundToPx()
            } else if (fraction < 0 && previousTab != null) {
                lerp(currentTab.width, previousTab.width, -fraction).roundToPx()
            } else {
                currentTab.width.roundToPx()
            }

            val indicatorOffset = if (fraction > 0 && nextTab != null) {
                lerp(currentTab.left, nextTab.left, fraction).roundToPx()
            } else if (fraction < 0 && previousTab != null) {
                lerp(currentTab.left, previousTab.left, -fraction).roundToPx()
            } else {
                currentTab.left.roundToPx()
            }

            val placeable = measurable.measure(
                Constraints(
                    minWidth = indicatorWidth,
                    maxWidth = indicatorWidth,
                    minHeight = 0,
                    maxHeight = constraints.maxHeight
                )
            )

            layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
                placeable.place(
                    indicatorOffset,
                    maxOf(constraints.minHeight - placeable.height, 0)
                )
            }
        }
    }
}
