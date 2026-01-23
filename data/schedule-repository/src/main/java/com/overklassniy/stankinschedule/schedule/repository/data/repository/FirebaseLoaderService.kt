package com.overklassniy.stankinschedule.schedule.repository.data.repository

import com.overklassniy.stankinschedule.schedule.core.data.mapper.toPairModel
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.repository.data.api.ScheduleRepositoryAPI
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.ScheduleLoaderService
import retrofit2.await
import javax.inject.Inject

class FirebaseLoaderService @Inject constructor(
    private val api: ScheduleRepositoryAPI,
) : FirebaseService(), ScheduleLoaderService {

    override suspend fun schedule(category: String, schedule: String): List<PairModel> {
        val ref = createRef(SCHEDULES_ROOT, category, schedule)
        val scheduleUrl = ref.downloadUrl.await().toString()
        return api.schedule(scheduleUrl).await().map { it.toPairModel() }
    }
}