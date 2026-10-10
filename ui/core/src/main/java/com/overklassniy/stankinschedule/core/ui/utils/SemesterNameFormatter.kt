package com.overklassniy.stankinschedule.core.ui.utils

import android.content.Context
import com.overklassniy.stankinschedule.core.ui.R

/**
 * Форматирует семестр в локализованную строку для отображения.
 *
 * API личного кабинета адресует семестры парой чисел (курс, семестр),
 * например course = 2, semester = 1 → «2 курс 1 семестр».
 *
 * @param context Контекст для доступа к строковым ресурсам.
 * @param course Номер курса.
 * @param semester Номер семестра внутри курса.
 * @return Локализованное название семестра.
 */
fun formatSemesterName(
    context: Context,
    course: Int,
    semester: Int,
): String {
    return context.getString(
        R.string.semester_course_format,
        course,
        semester,
    )
}
