package com.overklassniy.stankinschedule.schedule.table.domain.repository

import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.table.domain.model.TableConfig

/**
 * Создаёт визуализацию таблицы расписания в формате PDF и Bitmap на Android.
 */
interface AndroidTableCreator {

    /**
     * Создаёт PDF-документ с таблицей расписания.
     *
     * @param schedule Модель расписания.
     * @param config Конфигурация таблицы.
     * @return Готовый [PdfDocument].
     */
    fun createPdf(schedule: ScheduleModel, config: TableConfig): PdfDocument

    /**
     * Создаёт изображение (Bitmap) с таблицей расписания.
     *
     * @param schedule Модель расписания.
     * @param config Конфигурация таблицы.
     * @return Сгенерированный [Bitmap].
     */
    fun createImage(schedule: ScheduleModel, config: TableConfig): Bitmap
}