package com.overklassniy.stankinschedule.schedule.parser.domain.model

import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel

/**
 * Результат операции парсинга.
 */
sealed interface ParseResult {

    /**
     * Успешный результат парсинга.
     *
     * @property pair Распарсенная пара (занятие).
     */
    class Success(val pair: PairModel) : ParseResult {
        override fun toString(): String {
            return "Success(pair=$pair)"
        }
    }

    /**
     * Результат, указывающий на отсутствие необходимых данных.
     *
     * @property context Контекст, в котором произошла потеря данных.
     */
    class Missing(val context: String) : ParseResult {
        override fun toString(): String {
            return "Missing(context='$context')"
        }
    }

    /**
     * Результат с ошибкой парсинга.
     *
     * @property error Сообщение об ошибке.
     * @property context Контекст ошибки.
     */
    class Error(val error: String, val context: String) : ParseResult {
        override fun toString(): String {
            return "Error(error='$error', context='$context')"
        }
    }
}