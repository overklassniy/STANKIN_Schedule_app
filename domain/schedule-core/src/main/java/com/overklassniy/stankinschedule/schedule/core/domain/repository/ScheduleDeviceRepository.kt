package com.overklassniy.stankinschedule.schedule.core.domain.repository

import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel


/**
 * Репозиторий для работы с расписанием на устройстве (импорт/экспорт).
 */
interface ScheduleDeviceRepository {
    /**
     * Сохраняет расписание на устройство (в файл).
     *
     * @param model Модель расписания для сохранения.
     * @param path Путь к файлу или URI.
     */
    suspend fun saveToDevice(model: ScheduleModel, path: String)

    /**
     * Загружает расписание с устройства (из файла).
     *
     * @param path Путь к файлу или URI.
     * @return Загруженная модель расписания [ScheduleModel].
     */
    suspend fun loadFromDevice(path: String): ScheduleModel
}