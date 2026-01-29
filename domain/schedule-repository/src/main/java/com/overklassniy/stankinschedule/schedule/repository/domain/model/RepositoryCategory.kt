package com.overklassniy.stankinschedule.schedule.repository.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Категория репозитория расписания (например, курс или направление).
 *
 * @property name Название категории.
 * @property year Год (курс).
 */
data class RepositoryCategory(
    @SerializedName("name") val name: String,
    @SerializedName("year") val year: Int,
)