package com.overklassniy.stankinschedule.core.ui.ext

import android.content.res.Resources
import android.util.TypedValue

/**
 * Конвертирует значение в dp в пиксели.
 *
 * @param value Значение в dp.
 * @param resources Ресурсы для доступа к метрикам экрана.
 * @return Значение в пикселях.
 */
fun dpToPx(value: Float, resources: Resources): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics
    )
}

/**
 * Конвертирует значение в sp в пиксели.
 *
 * @param value Значение в sp.
 * @param resources Ресурсы для доступа к метрикам экрана.
 * @return Значение в пикселях.
 */
fun spToPx(value: Float, resources: Resources): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics
    )
}