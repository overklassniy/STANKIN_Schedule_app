package com.overklassniy.stankinschedule.core.domain.model

/**
 * Модель данных об обновлении приложения.
 *
 * @property latestVersion Последняя доступная версия.
 * @property changelog Описание изменений (release notes).
 * @property downloadUrl Ссылка на страницу релиза.
 * @property releaseName Название релиза.
 */
data class AppUpdate(
    val latestVersion: String,
    val changelog: String,
    val downloadUrl: String,
    val releaseName: String
)