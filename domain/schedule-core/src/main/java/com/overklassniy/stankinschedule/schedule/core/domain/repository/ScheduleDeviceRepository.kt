package com.overklassniy.stankinschedule.schedule.core.domain.repository

import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel


interface ScheduleDeviceRepository {
    suspend fun saveToDevice(model: ScheduleModel, path: String)
    suspend fun loadFromDevice(path: String): ScheduleModel
}