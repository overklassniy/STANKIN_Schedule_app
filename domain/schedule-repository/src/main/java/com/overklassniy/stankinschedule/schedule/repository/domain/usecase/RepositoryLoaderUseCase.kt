package com.overklassniy.stankinschedule.schedule.repository.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.ScheduleLoaderService
import javax.inject.Inject

/**
 * UseCase для загрузки конкретного расписания.
 *
 * Получает пары из удаленного источника, формирует модель расписания и
 * сохраняет её в локальном хранилище.
 */
class RepositoryLoaderUseCase @Inject constructor(
    private val remoteService: ScheduleLoaderService,
    private val scheduleStorage: ScheduleStorage
) {

    /**
     * Загружает расписание, парсит его и сохраняет в локальную базу данных.
     *
     * @param category Категория расписания.
     * @param path Путь к файлу расписания.
     * @param scheduleName Название расписания (группы).
     * @param replaceExist Перезаписывать ли существующее расписание.
     */
    suspend fun loadSchedule(
        category: String,
        path: String,
        scheduleName: String,
        replaceExist: Boolean = true
    ) {
        val pairs = remoteService.schedule(category, path)
        val model = ScheduleModel(info = ScheduleInfo(scheduleName)).apply {
            pairs.forEach { add(it) }
        }
        scheduleStorage.saveSchedule(model, replaceExist)
    }

    /**
     * Скачивает файл расписания (например, PDF) на устройство.
     *
     * @param category Категория расписания.
     * @param path Путь к файлу.
     * @param fileName Имя сохраняемого файла.
     * @return Локальный путь к скачанному файлу.
     */
    suspend fun downloadScheduleFile(
        category: String,
        path: String,
        fileName: String
    ): String {
        return remoteService.downloadScheduleFile(category, path, fileName)
    }
}