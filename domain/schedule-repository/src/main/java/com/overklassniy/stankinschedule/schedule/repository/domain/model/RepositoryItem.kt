package com.overklassniy.stankinschedule.schedule.repository.domain.model

/**
 * Элемент репозитория расписания (конкретное расписание группы).
 *
 * @property name Название расписания (например, "ИДБ-20-12").
 * @property path Путь к файлу расписания на сервере.
 * @property category Категория, к которой относится расписание.
 */
data class RepositoryItem(
    val name: String,
    val path: String,
    val category: String,
)