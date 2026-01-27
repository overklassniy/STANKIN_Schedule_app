package com.overklassniy.stankinschedule.schedule.repository.domain.model

import com.google.gson.annotations.SerializedName

data class RepositoryDescription(
    @SerializedName("lastUpdate") val lastUpdate: String,
    @SerializedName("categories") val categories: List<RepositoryCategory>,
)