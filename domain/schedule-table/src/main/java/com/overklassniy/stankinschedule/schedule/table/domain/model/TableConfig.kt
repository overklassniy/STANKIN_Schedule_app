package com.overklassniy.stankinschedule.schedule.table.domain.model

import android.graphics.Color

/**
 * Конфигурация отрисовки таблицы расписания.
 *
 * @property color Цвет таблицы.
 * @property longScreenSize Длина высокой стороны экрана (для масштабирования).
 * @property mode Режим отображения.
 * @property page Номер страницы.
 */
data class TableConfig(
    val color: Int,
    val longScreenSize: Float,
    val mode: TableMode,
    val page: Int
) {
    companion object {
        /**
         * Возвращает конфигурацию по умолчанию.
         *
         * @return [TableConfig] с чёрным цветом, высотой 720f, полным режимом и страницей 0.
         */
        fun default() = TableConfig(Color.BLACK, 720f, TableMode.Full, 0)
    }
}