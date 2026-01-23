package com.overklassniy.stankinschedule.schedule.repository.ui.components

import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem

sealed interface DownloadEvent {
    class StartDownload(val scheduleName: String, val item: RepositoryItem) : DownloadEvent
}