package com.overklassniy.stankinschedule.schedule.repository.data.mapper

import com.overklassniy.stankinschedule.schedule.repository.data.api.DescriptionResponse
import com.overklassniy.stankinschedule.schedule.repository.data.api.ScheduleItemResponse
import com.overklassniy.stankinschedule.schedule.repository.data.db.RepositoryEntity
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryCategory
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem


fun ScheduleItemResponse.toItem(category: String): RepositoryItem {
    return RepositoryItem(
        name = name,
        path = path,
        category = category
    )
}

fun RepositoryEntity.toItem(): RepositoryItem {
    return RepositoryItem(
        name = name,
        path = path,
        category = category
    )
}

fun RepositoryItem.toEntity(): RepositoryEntity {
    return RepositoryEntity(
        name = name,
        path = path,
        category = category
    )
}

fun DescriptionResponse.toDescription(): RepositoryDescription {
    return RepositoryDescription(
        lastUpdate = lastUpdate,
        categories = categories.map { RepositoryCategory(it.name, it.year) }
    )
}
