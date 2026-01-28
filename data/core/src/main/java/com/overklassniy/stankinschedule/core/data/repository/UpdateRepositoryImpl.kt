package com.overklassniy.stankinschedule.core.data.repository

import android.util.Log
import com.overklassniy.stankinschedule.core.data.api.GitHubApi
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
            val latestVersion = release.tag_name.removePrefix("v")

            if (isNewerVersion(latestVersion, currentVersion)) {
                AppUpdate(
                    latestVersion = latestVersion,
                    changelog = release.body,
                    downloadUrl = release.html_url,
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

    /**
     * Сравнивает версии в формате X.Y.Z.
     *
     * @return true если latestVersion > currentVersion
     */
    private fun isNewerVersion(latestVersion: String, currentVersion: String): Boolean {
        val latest = latestVersion.split(".").mapNotNull { it.toIntOrNull() }
        val current = currentVersion.split(".").mapNotNull { it.toIntOrNull() }

        for (i in 0 until maxOf(latest.size, current.size)) {
            val l = latest.getOrElse(i) { 0 }
            val c = current.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}