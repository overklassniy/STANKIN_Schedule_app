package com.overklassniy.stankinschedule.schedule.table.domain.model

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.graphics.withRotation

/**
 * Временно устанавливает размер шрифта для [Paint], выполняет отрисовку и восстанавливает исходное значение.
 *
 * @param fontSize Новый размер шрифта.
 * @param draw Блок отрисовки, получающий настроенный [Paint].
 * @return Результат выполнения блока [draw].
 */
fun <R, P : Paint> P.withSize(fontSize: Float, draw: (paint: P) -> R): R {
    val prevSize = this.textSize
    this.textSize = fontSize
    val result = draw(this)
    this.textSize = prevSize
    return result
}

/**
 * Возвращает смещение по базовой линии для центрирования текста по вертикали.
 *
 * @return Смещение базовой линии.
 */
fun Paint.centerBaseline(): Float = (descent() + ascent()) / 2f

/**
 * Возвращает высоту строки текста по метрикам шрифта.
 *
 * @return Высота строки.
 */
fun Paint.lineHeight(): Float = fontMetrics.run { descent - ascent }

/**
 * Подбирает максимально возможный размер шрифта, чтобы высота строки не превышала [height].
 *
 * @param height Требуемая высота строки.
 * @param textPaint Кисть для измерения метрик шрифта.
 * @return Подходящий размер шрифта.
 */
fun fontSizeForHeight(height: Float, textPaint: Paint): Float {
    var fontSize = 1f
    while (true) {
        textPaint.textSize = fontSize + 1f
        val extra = textPaint.fontMetrics.run { descent - ascent }

        if (extra >= height) {
            break
        }

        fontSize += 1f
    }

    return fontSize
}

/**
 * Строит многострочный [StaticLayout] с переносами и многоточием, уменьшая шрифт
 * до вписывания в заданные размеры.
 *
 * @param text Текст.
 * @param width Максимальная ширина.
 * @param height Максимальная высота.
 * @param fontSize Базовый размер шрифта.
 * @param paint Кисть для текста.
 * @return Готовый [StaticLayout].
 */
fun prepareMultilineLayout(
    text: String,
    width: Int,
    height: Int,
    fontSize: Float,
    paint: TextPaint
): StaticLayout {
    var layout: StaticLayout

    val textPaint = TextPaint(paint)
    var currentFontSize = fontSize

    while (true) {
        textPaint.textSize = currentFontSize

        layout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
            .setEllipsizedWidth(width)
            .setEllipsize(TextUtils.TruncateAt.END)
            .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL)
            .build()

        if (layout.height <= height || currentFontSize < 3f) {
            break
        }

        currentFontSize -= 0.5f
    }

    return layout
}

/**
 * Рисует текст по центру прямоугольной области. При необходимости поворачивает.
 *
 * @param text Текст для отрисовки.
 * @param x Левая координата области.
 * @param y Верхняя координата области.
 * @param w Ширина области.
 * @param h Высота области.
 * @param fontSize Размер шрифта.
 * @param paint Кисть для текста.
 * @param rotate Угол поворота (в градусах), по умолчанию 0.
 */
fun Canvas.drawCenterText(
    text: String,
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    fontSize: Float,
    paint: Paint,
    rotate: Int = 0
) = paint.withSize(fontSize) { textPaint ->
    val textWidth = paint.measureText(text) / 2
    val centerX = x + w / 2 - textWidth
    val centerY = y + h / 2 - textPaint.centerBaseline()

    if (rotate == 0) {
        drawText(text, centerX, centerY, textPaint)
    } else {
        withRotation(
            degrees = rotate.toFloat(),
            pivotX = x + w / 2,
            pivotY = y + h / 2
        ) {
            drawText(text, centerX, centerY, textPaint)
        }
    }
}

/**
 * Рисует текст, центрируя его относительно точки (x, y). Возвращает высоту строки.
 *
 * @param text Текст для отрисовки.
 * @param x Координата X центра.
 * @param y Координата Y центра.
 * @param fontSize Размер шрифта.
 * @param paint Кисть для текста.
 * @return Высота строки.
 */
fun Canvas.drawText(
    text: String,
    x: Float,
    y: Float,
    fontSize: Float,
    paint: Paint
): Float = paint.withSize(fontSize) { textPaint ->
    val textWidth = paint.measureText(text)
    drawText(text, x - textWidth / 2, y - textPaint.centerBaseline(), textPaint)
    textPaint.lineHeight()
}
