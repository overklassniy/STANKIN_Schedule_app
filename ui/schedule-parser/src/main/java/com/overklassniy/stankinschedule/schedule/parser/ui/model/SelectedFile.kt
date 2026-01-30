package com.overklassniy.stankinschedule.schedule.parser.ui.model

import android.net.Uri

/**
 * Выбранный пользователем PDF-файл расписания.
 *
 * @property path Uri к файлу.
 * @property filename Имя файла без расширения.
 */
class SelectedFile(
    val path: Uri,
    val filename: String
)