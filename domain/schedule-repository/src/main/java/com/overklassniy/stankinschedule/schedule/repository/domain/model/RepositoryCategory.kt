package com.overklassniy.stankinschedule.schedule.repository.domain.model

import com.google.gson.annotations.SerializedName

data class RepositoryCategory(
    @SerializedName("name") val name: String,
    @SerializedName("year") val year: Int,
)