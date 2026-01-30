package com.overklassniy.stankinschedule.journal.viewer.ui.components

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.overklassniy.stankinschedule.journal.core.domain.model.SemesterMarks
import com.overklassniy.stankinschedule.journal.viewer.ui.view.MarksTableView

/**
 * Таблица оценок семестра.
 *
 * Обёртка над нативным [MarksTableView] через [AndroidView]. Обновляет цвет текста
 * и данные таблицы при изменении параметров.
 *
 * @param semesterMarks Данные по дисциплинам и оценкам за семестр.
 * @param textColor Цвет текста (ARGB) для рендера таблицы.
 * @param modifier Модификатор для внешнего оформления.
 */
@Composable
fun MarksTable(
    semesterMarks: SemesterMarks,
    textColor: Int,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            MarksTableView(
                context = context
            ).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        },
        update = {
            it.setTextColor(textColor)
            it.setSemesterMarks(semesterMarks)
        },
        modifier = modifier
    )
}
