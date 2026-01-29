package com.overklassniy.stankinschedule.schedule.repository.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Описание репозитория расписания.
 *
 * Содержит метаданные о последнем обновлении и доступных категориях.
 *
 * @property lastUpdate Дата и время последнего обновления данных на сервере.
 * @property categories Список доступных категорий расписания.
 */
data class RepositoryDescription(
    @SerializedName("lastUpdate") val lastUpdate: String,
    @SerializedName("categories") val categories: List<RepositoryCategory>,
)