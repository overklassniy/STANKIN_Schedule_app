package com.overklassniy.stankinschedule.journal.viewer.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity

enum class SwipingStates {
    EXPANDED,
    COLLAPSED
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollapseLayout(
    headerHeight: Dp,
    header: @Composable ColumnScope.(progress: Float) -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val heightInPx = with(density) { headerHeight.toPx() }

    val state = remember {
        AnchoredDraggableState(initialValue = SwipingStates.EXPANDED)
    }

    SideEffect {
        state.updateAnchors(
            DraggableAnchors {
                SwipingStates.COLLAPSED at 0f
                SwipingStates.EXPANDED at heightInPx
            }
        )
    }

    val nestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                return if (delta < 0) {
                    val consumed = state.dispatchRawDelta(delta)
                    Offset(0f, consumed)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                val consumedDelta = state.dispatchRawDelta(delta)
                return Offset(0f, consumedDelta)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return if (available.y < 0) {
                    state.settle(tween(durationMillis = 300))
                    available
                } else {
                    Velocity.Zero
                }
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity,
            ): Velocity {
                state.settle(tween(durationMillis = 300))
                return super.onPostFling(consumed, available)
            }
        }
    }

    val progress by remember {
        derivedStateOf {
            val offset = state.offset
            if (offset.isNaN()) 1f else (offset / heightInPx).coerceIn(0f, 1f)
        }
    }

    Column(
        modifier = modifier
            .anchoredDraggable(
                state = state,
                orientation = Orientation.Vertical
            )
            .nestedScroll(nestedScroll)
    ) {
        header(progress)
        content()
    }
}
