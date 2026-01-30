package com.overklassniy.stankinschedule.schedule.viewer.ui.components

import android.net.Uri

/**
 * Состояние процесса сохранения расписания.
 *
 * Используется для отображения прогресса и результата в UI.
 *
 * - Nothing — нет активной операции
 * - Finished — операция завершена, доступен путь и формат
 * - Error — произошла ошибка, доступен Throwable
 */
sealed interface ExportProgress {
    /** Отсутствие активной операции экспорта. */
    object Nothing : ExportProgress

    /** Успешное завершение экспорта.
     * @property path Путь к сохраненному файлу.
     * @property format Формат сохраненного файла.
     */
    class Finished(val path: Uri, val format: ExportFormat) : ExportProgress

    /** Ошибка экспорта.
     * @property error Причина ошибки. Может быть показана пользователю.
     */
    class Error(val error: Throwable) : ExportProgress
}