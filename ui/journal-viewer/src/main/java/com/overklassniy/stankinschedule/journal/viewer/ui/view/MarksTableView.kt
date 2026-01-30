package com.overklassniy.stankinschedule.journal.viewer.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.overklassniy.stankinschedule.core.ui.ext.dpToPx
import com.overklassniy.stankinschedule.core.ui.ext.spToPx
import com.overklassniy.stankinschedule.journal.core.domain.model.Discipline
import com.overklassniy.stankinschedule.journal.core.domain.model.MarkType
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.viewer.ui.R
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Кастомное представление таблицы оценок семестра.
 *
 * Рисует названия дисциплин, их оценки по типам и коэффициенты, а также
 * строки с текущим и накопленным рейтингом. Поддерживает XML‑атрибуты
 * из declare‑styleable MarksTableView: mt_dividerColor, mt_textColor,
 * mt_accumulateRating, mt_rating.
 */
class MarksTableView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttrs: Int = 0, defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttrs, defStyleRes) {

    private val minCellSize = dpToPx(25F, context.resources)
    private val textCellMargin = dpToPx(4F, context.resources)
    private val markCellMargin = dpToPx(1F, context.resources)
    private val bitmapNoMark = ContextCompat.getDrawable(context, R.drawable.no_mark)?.toBitmap()!!
        .scale(minCellSize.toInt(), minCellSize.toInt(), false)
    private var totalDisciplineSize = 0F
    private var totalHeaderSize = 0F
    private var maxWrapHeight = 0f
    private val markHeaderData = listOf("М1", "М2", "К", "З", "Э", "К")
    private var marksData = emptyData()
    private val disciplineLayouts = arrayListOf<StaticLayout>()
    private val ratingLayout = arrayListOf<StaticLayout>()
    private val headerLayout = arrayListOf<Float>()
    private var headerHeight = 0F
    private var ratingText: String = "Рейтинг"
    private var accumulateRatingText: String = "Накопленный рейтинг"
    private val contentPainter = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val disciplinePainter = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val ratingPainter = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val linePainter = Paint(Paint.ANTI_ALIAS_FLAG)
    private val drawablePainter = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        context.withStyledAttributes(
            attrs, R.styleable.MarksTableView, defStyleAttrs, defStyleRes
        ) {

            // Стиль линий
            val dividerColor = R.styleable.MarksTableView_mt_dividerColor
            if (hasValue(dividerColor)) {
                linePainter.color = getColor(dividerColor, 0)
            }
            linePainter.strokeWidth = dpToPx(0.5F, context.resources)
            linePainter.style = Paint.Style.STROKE

            drawablePainter.strokeWidth = dpToPx(0.5F, context.resources)
            drawablePainter.style = Paint.Style.STROKE

            // Стиль ячеек
            val textColor = R.styleable.MarksTableView_mt_textColor
            if (hasValue(textColor)) {
                val color = getColor(textColor, 0)
                contentPainter.color = color
                disciplinePainter.color = color
                ratingPainter.color = color
            }

            contentPainter.textSize = spToPx(14F, context.resources)
            disciplinePainter.textSize = spToPx(14F, context.resources)
            ratingPainter.textSize = spToPx(14F, context.resources)

            contentPainter.textAlign = Paint.Align.CENTER
            ratingPainter.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    }

    /**
     * Измеряет размеры таблицы исходя из содержимого: хедера типов оценок,
     * списка дисциплин с оценками и строк рейтинга.
     *
     * Рассчитывает ширины столбцов, высоту строк и конечные размеры View.
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        headerLayout.clear()
        disciplineLayouts.clear()
        ratingLayout.clear()

        val widthSize = resolveSize(100, widthMeasureSpec)
        var wrapHeight = dpToPx(0.5F, context.resources) // Толщина нижней разделительной линии

        // Хедер с типами оценок (М1, М2, К, З, Э, К)
        val fontMetrics = contentPainter.fontMetrics
        headerHeight = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading
        val verMargin = textCellMargin * 2
        val horTextMargin = textCellMargin * 2
        val horMarkMargin = markCellMargin * 2
        wrapHeight += headerHeight + verMargin

        for (type in markHeaderData) {
            val headerSize = max(minCellSize, contentPainter.measureText(type)) + horMarkMargin
            headerLayout.add(headerSize)
        }

        // Ячейки с оценками и коэффициентом
        for (discipline in marksData) {
            // Оценки
            for ((j, type) in MarkType.entries.withIndex()) {
                val mark = discipline[type]
                if (mark != null) {
                    val markSize = contentPainter.measureText(mark.toString()) + horMarkMargin
                    if (markSize > headerLayout[j]) {
                        headerLayout[j] = markSize
                    }
                }
            }
            // Коэффициент
            val factorHolder =
                if (discipline.factor == Discipline.NO_FACTOR) " " else discipline.factor.toString()
            val factorSize = contentPainter.measureText(factorHolder) + horMarkMargin
            if (factorSize > headerLayout.last()) {
                headerLayout[headerLayout.size - 1] = factorSize
            }
        }
        totalHeaderSize = headerLayout.sum()

        // Заголовок с дисциплинами
        totalDisciplineSize = (widthSize - totalHeaderSize)
        val maxDisciplineTextSize = (totalDisciplineSize - horTextMargin).toInt()

        for (discipline in marksData) {
            val title = discipline.title
            val layout = StaticLayout.Builder
                .obtain(title, 0, title.length, disciplinePainter, maxDisciplineTextSize)
                .build()

            disciplineLayouts.add(layout)
            wrapHeight += layout.height + verMargin
        }

        // Строки рейтинга (текущий и накопленный)
        for (rating in listOf(ratingText, accumulateRatingText)) {
            val layout = StaticLayout.Builder
                .obtain(rating, 0, rating.length, ratingPainter, maxDisciplineTextSize)
                .build()
            ratingLayout.add(layout)
            wrapHeight += layout.height + verMargin
        }

        // Установка конечных размеров
        maxWrapHeight = wrapHeight
        val heightSize = resolveSize(wrapHeight.roundToInt(), heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
    }

    /**
     * Отрисовывает таблицу оценок: хедер типов, строки дисциплин с оценками
     * и коэффициентами, а также строки текущего и накопленного рейтинга.
     */
    override fun onDraw(canvas: Canvas) {
        val fontExtra = (contentPainter.descent() + contentPainter.ascent()) / 2

        // Рисуем хедер типов оценок
        var offset = totalDisciplineSize
        for ((size, data) in headerLayout.zip(markHeaderData)) {
            canvas.drawText(
                data,
                offset + size / 2,
                textCellMargin + headerHeight / 2F - fontExtra,
                contentPainter
            )
            canvas.drawLine(offset, 0F, offset, maxWrapHeight, linePainter)
            offset += size
        }

        canvas.drawLine(0F, 0F, measuredWidth.toFloat(), 0F, linePainter)

        // Подводящая линия заголовка таблицы
        canvas.translate(0F, headerHeight + textCellMargin * 2)
        canvas.drawLine(0F, 0F, measuredWidth.toFloat(), 0F, linePainter)
        canvas.translate(textCellMargin, textCellMargin)

        // Рисование дисциплин, их оценок и коэффициент
        for ((layout, discipline) in disciplineLayouts.zip(marksData)) {
            layout.draw(canvas)

            // Оценки дисциплины
            var markOffset = totalDisciplineSize - textCellMargin
            for ((markType, size) in MarkType.entries.toTypedArray().zip(headerLayout)) {
                val mark = discipline[markType]
                when {
                    // Пустое значение (т.е. крестик)
                    mark == null -> {
                        val halfImageSize = minCellSize / 2
                        canvas.drawBitmap(
                            bitmapNoMark,
                            markOffset + size / 2 - halfImageSize,
                            layout.height / 2 - halfImageSize,
                            drawablePainter
                        )
                    }
                    // Есть оценка
                    mark != Discipline.NO_MARK -> {
                        canvas.drawText(
                            mark.toString(),
                            markOffset + size / 2,
                            layout.height / 2 - fontExtra,
                            contentPainter
                        )
                    }
                }
                markOffset += size
            }

            // Коэффициент дисциплины
            canvas.drawText(
                discipline.factor.toString(),
                markOffset + headerLayout.last() / 2,
                layout.height / 2 - fontExtra,
                contentPainter
            )

            drawLineAndMove(canvas, layout)
        }

        // Рейтинг
        for ((value, layout) in listOf(marksData.rating, marksData.accumulatedRating).zip(
            ratingLayout
        )) {
            layout.draw(canvas)

            var ratingOffset = totalDisciplineSize - textCellMargin
            for ((i, size) in headerLayout.withIndex()) {
                if (i == 0) {
                    if (value != null && value != Discipline.NO_MARK) {
                        canvas.drawText(
                            value.toString(),
                            ratingOffset + size / 2,
                            layout.height / 2 - fontExtra,
                            contentPainter
                        )
                    }
                } else {
                    val halfImageSize = minCellSize / 2
                    canvas.drawBitmap(
                        bitmapNoMark,
                        ratingOffset + size / 2 - halfImageSize,
                        layout.height / 2 - halfImageSize,
                        drawablePainter
                    )
                }
                ratingOffset += size
            }

            drawLineAndMove(canvas, layout)
        }
    }

    /**
     * Рисует горизонтальную разделительную линию под текущим блоком и
     * смещает канву на высоту блока с отступами.
     *
     * @param canvas Канва для рисования.
     * @param layout Текущий текстовый блок, определяющий высоту смещения.
     */
    private fun drawLineAndMove(canvas: Canvas, layout: StaticLayout) {
        canvas.translate(0F, layout.height + textCellMargin)
        canvas.drawLine(-textCellMargin, 0F, measuredWidth.toFloat(), 0F, linePainter)
        canvas.translate(0F, textCellMargin)
    }

    /**
     * Устанавливает данные семестра для отображения таблицы и запрашивает переразметку.
     *
     * @param data Данные семестра (дисциплины, оценки, рейтинги).
     */
    fun setSemesterMarks(data: SemesterMarks) {
        marksData = data
        requestLayout()
    }

    /**
     * Устанавливает цвет текста для всех элементов таблицы.
     *
     * @param color Цвет в формате ARGB.
     */
    fun setTextColor(color: Int) {
        contentPainter.color = color
        disciplinePainter.color = color
        ratingPainter.color = color
    }

    private fun emptyData() = SemesterMarks(arrayListOf(), null, null)
}