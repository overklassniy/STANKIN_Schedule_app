package com.overklassniy.stankinschedule.schedule.repository.data.mapper

import com.overklassniy.stankinschedule.schedule.repository.data.db.RepositoryEntity
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem

/**
 * Преобразует сущность базы данных [RepositoryEntity] в модель предметной области [RepositoryItem].
 *
 * @return Объект [RepositoryItem] с данными из сущности БД.
 */
fun RepositoryEntity.toItem(): RepositoryItem {
    return RepositoryItem(
        name = name,
        path = path,
        category = category
    )
}

/**
 * Преобразует модель предметной области [RepositoryItem] в сущность базы данных [RepositoryEntity].
 *
 * @return Объект [RepositoryEntity] для сохранения в БД.
 */
fun RepositoryItem.toEntity(): RepositoryEntity {
    return RepositoryEntity(
        name = name,
        path = path,
        category = category
    )
}
