package com.overklassniy.stankinschedule.schedule.table.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

/**
 * Контейнер с поддержкой масштабирования и панорамирования.
 *
 * Формирует UI: Box, который обрабатывает жесты масштабирования и перемещения.
 * Дочерний контент получает текущие значения scale и смещения.
 *
 * @param modifier Модификатор контейнера.
 * @param minScale Минимальный допустимый масштаб. Значение больше 0.
 * @param maxScale Максимальный допустимый масштаб. Значение больше minScale.
 * @param onTap Колбэк одиночного тапа. Используется для скрытия или показа UI.
 * @param content Содержимое. Принимает scale, offsetX, offsetY для позиционирования.
 * @return Ничего не возвращает. Управляет состоянием жестов.
 */
@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    minScale: Float = 0.1f,
    maxScale: Float = 5f,
    onTap: (() -> Unit)? = null,
    content: @Composable BoxScope.(scale: Float, offsetX: Float, offsetY: Float) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }

    // Обновляет смещения, ограничивая панорамирование границами контента.
    // maxX и maxY рассчитываются как половина превышения размера при текущем масштабе.
    val processOffsets: (panX: Float, panY: Float) -> Unit = { panX, panY ->
        val maxX = (size.width * (scale - 1)) / 2
        val minX = -maxX

        offsetX = maxOf(minX, minOf(maxX, offsetX + panX))
        val maxY = (size.height * (scale - 1)) / 2
        val minY = -maxY
        offsetY = maxOf(minY, minOf(maxY, offsetY + panY))
    }

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = maxOf(minScale, minOf(scale * zoom, maxScale))
                    processOffsets(pan.x, pan.y)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap?.invoke() },
                    onDoubleTap = {
                        // Двойной тап переключает масштаб между базовым и промежуточным значением.
                        // Выбор maxScale / 2 обеспечивает быстрое приближение без предельного увеличения.
                        scale = if (scale == minScale) {
                            maxScale / 2
                        } else {
                            minScale
                        }
                        // Сброс смещений при смене масштаба.
                        processOffsets(0f, 0f)
                    }
                )
            }
    ) {
        this.content(scale, offsetX, offsetY)
    }
}