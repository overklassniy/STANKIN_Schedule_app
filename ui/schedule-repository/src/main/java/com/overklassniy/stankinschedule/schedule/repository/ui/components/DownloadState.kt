package com.overklassniy.stankinschedule.schedule.repository.ui.components

import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem

sealed interface DownloadState {
    class StartDownload(val scheduleName: String, val item: RepositoryItem) :
        DownloadState

    class RequiredName(@Suppress("unused") val scheduleName: String, val item: RepositoryItem) :
        DownloadState
}