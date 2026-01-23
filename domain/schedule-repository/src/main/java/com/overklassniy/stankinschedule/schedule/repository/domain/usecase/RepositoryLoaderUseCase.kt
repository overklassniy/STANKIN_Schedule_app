package com.overklassniy.stankinschedule.schedule.repository.domain.usecase

import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.ScheduleLoaderService
import javax.inject.Inject

class RepositoryLoaderUseCase @Inject constructor(
    private val remoteService: ScheduleLoaderService,
    private val scheduleStorage: ScheduleStorage
) {

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
}