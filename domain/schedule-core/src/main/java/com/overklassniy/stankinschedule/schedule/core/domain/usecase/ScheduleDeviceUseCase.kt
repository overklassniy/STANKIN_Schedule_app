package com.overklassniy.stankinschedule.schedule.core.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleDeviceRepository
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ScheduleDeviceUseCase @Inject constructor(
    private val storage: ScheduleStorage,
    private val device: ScheduleDeviceRepository
) {

    /**
     * Сохраняет расписание на устройство.
     *
     * @param scheduleId ID расписания для сохранения
     * @param path Путь для сохранения файла
     * @return Flow с true при успешном сохранении
     * @throws RuntimeException если расписание не найдено
     */
    fun saveToDevice(scheduleId: Long, path: String): Flow<Boolean> = flow {
        val schedule = storage.scheduleModel(scheduleId).first()
            ?: throw RuntimeException("Schedule not found")
        device.saveToDevice(schedule, path)
        emit(true)
    }.flowOn(Dispatchers.IO)

    /**
     * Загружает расписание с устройства и сохраняет его в хранилище.
     *
     * @param path Путь к файлу расписания
     * @return Flow с названием загруженного расписания
     */
    fun loadFromDevice(path: String): Flow<String> = flow {
        val schedule = device.loadFromDevice(path)
        storage.saveSchedule(schedule)
        emit(schedule.info.scheduleName)
    }.flowOn(Dispatchers.IO)
}