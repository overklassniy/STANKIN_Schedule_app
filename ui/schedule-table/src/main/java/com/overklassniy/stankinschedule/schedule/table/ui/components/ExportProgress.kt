package com.overklassniy.stankinschedule.schedule.table.ui.components

import android.net.Uri

/**
 * Состояние процесса экспорта.
 *
 * Назначение: описывает текущую стадию операции отправки или сохранения файла расписания.
 *
 * Возможные состояния:
 * - Nothing — процесс не запущен
 * - Running — процесс в работе
 * - Finished — процесс завершен успешно, доступен результат
 * - Error — произошла ошибка, доступен объект исключения
 */
sealed interface ExportProgress {
    /** Нет активного процесса экспорта. */
    object Nothing : ExportProgress

    /** Экспорт запущен и выполняется. */
    object Running : ExportProgress

    /**
     * Успешное завершение экспорта.
     *
     * @property path URI к сохраненному или подготовленному файлу
     * @property type тип операции экспорта (отправка или сохранение)
     * @property format выбранный формат файла
     */
    class Finished(
        val path: Uri,
        val type: ExportType,
        val format: ExportFormat
    ) : ExportProgress

    /**
     * Ошибка при экспорте.
     *
     * @property error исходное исключение, содержащее подробности причины
     */
    class Error(val error: Throwable) : ExportProgress
}