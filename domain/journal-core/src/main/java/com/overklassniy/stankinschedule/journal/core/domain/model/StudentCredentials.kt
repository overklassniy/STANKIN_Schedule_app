package com.overklassniy.stankinschedule.journal.core.domain.model

/**
 * Учетные данные студента для доступа к журналу.
 *
 * @property login Логин.
 * @property password Пароль.
 */
data class StudentCredentials(
    val login: String,
    val password: String,
)