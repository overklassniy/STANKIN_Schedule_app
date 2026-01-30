package com.overklassniy.stankinschedule.schedule.table.ui.components

/**
 * Форматы экспорта расписания.
 *
 * Назначение: определяет тип выходного файла при отправке или сохранении.
 *
 * @property memeType MIME тип файла, применяемый при сохранении и шаринге.
 * Инварианты: значение соответствует официальному регистрационному MIME.
 */
enum class ExportFormat(val memeType: String) {
    /** Изображение JPEG. Подходит для быстрой отправки и просмотра. */
    Image("image/jpeg"),

    /** Документ PDF. Подходит для печати и многостраничных представлений. */
    Pdf("application/pdf");
}