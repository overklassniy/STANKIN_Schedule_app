/**
 * События импорта расписания из файла.
 *
 * Используется как контракт, если импорт запускается через внутреннюю шину событий.
 */
package com.overklassniy.stankinschedule.schedule.creator.ui.components

import android.net.Uri

/**
 * Контракт событий импорта.
 */
@Suppress("Unused")
interface ImportEvent {

    /**
     * Отмена операции импорта.
     */
    object Cancel : ImportEvent

    /**
     * Импорт выбранного файла.
     *
     * @property uri Адрес выбранного документа.
     */
    class Import(val uri: Uri) : ImportEvent
}