package com.overklassniy.stankinschedule.schedule.repository.domain.repository

import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel

/**
 * Сервис для загрузки и парсинга файлов расписания.
 */
interface ScheduleLoaderService {

    /**
     * Загружает и парсит расписание.
     *
     * @param category Категория расписания.
     * @param schedule Путь к файлу расписания.
     * @return Список пар [PairModel].
     */
    suspend fun schedule(category: String, schedule: String): List<PairModel>

    /**
     * Скачивает файл расписания и сохраняет его локально.
     *
     * @param category Категория расписания.
     * @param schedule Путь к файлу расписания.
     * @param fileName Имя для сохранения файла.
     * @return Путь к сохраненному файлу.
     */
    suspend fun downloadScheduleFile(category: String, schedule: String, fileName: String): String
}