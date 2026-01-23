package com.overklassniy.stankinschedule.schedule.repository.domain.repository

import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel

interface ScheduleLoaderService {

    suspend fun schedule(category: String, schedule: String): List<PairModel>

}