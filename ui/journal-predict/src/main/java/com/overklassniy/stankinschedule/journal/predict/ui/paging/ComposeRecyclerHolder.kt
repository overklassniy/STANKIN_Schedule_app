package com.overklassniy.stankinschedule.journal.predict.ui.paging

import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.recyclerview.widget.RecyclerView

/**
 * Базовый RecyclerView.ViewHolder для хранения ComposeView.
 *
 * Устанавливает стратегию композиции [ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed]
 * для корректной очистки ресурсов при уничтожении жизненного цикла.
 */
abstract class ComposeRecyclerHolder(
    val composeView: ComposeView,
) : RecyclerView.ViewHolder(composeView) {

    init {
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
    }
}