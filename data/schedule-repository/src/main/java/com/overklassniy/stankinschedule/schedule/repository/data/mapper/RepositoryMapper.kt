package com.overklassniy.stankinschedule.schedule.repository.data.mapper

import com.overklassniy.stankinschedule.schedule.repository.data.db.RepositoryEntity
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem

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
