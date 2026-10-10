package com.overklassniy.stankinschedule.core.data.repository

import android.util.Log
import com.overklassniy.stankinschedule.core.data.api.GitHubApi
import com.overklassniy.stankinschedule.core.domain.ext.isOlderVersionThan
import com.overklassniy.stankinschedule.core.domain.model.AppUpdate
import com.overklassniy.stankinschedule.core.domain.repository.UpdateRepository
import javax.inject.Inject

/**
 * Реализация репозитория для проверки обновлений через GitHub API.
 */
class UpdateRepositoryImpl @Inject constructor(
    private val gitHubApi: GitHubApi
) : UpdateRepository {

    override suspend fun checkForUpdate(currentVersion: String): AppUpdate? {
        return try {
            val release = gitHubApi.getLatestRelease()
            val latestVersion = release.tagName.removePrefix("v")

            if (currentVersion isOlderVersionThan latestVersion) {
                AppUpdate(
                    latestVersion = latestVersion,
                    changelog = release.body,
                    downloadUrl = release.htmlUrl,
                    releaseName = release.name
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UpdateRepository", "Failed to check for updates", e)
            null
        }
    }
}
