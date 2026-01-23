package com.overklassniy.stankinschedule.schedule.repository.domain.repository

import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem


interface ScheduleRemoteService {

    suspend fun description(): RepositoryDescription

    suspend fun category(category: String): List<RepositoryItem>

}