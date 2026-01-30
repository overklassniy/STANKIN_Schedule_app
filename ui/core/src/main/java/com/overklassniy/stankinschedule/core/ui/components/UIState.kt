package com.overklassniy.stankinschedule.core.ui.components

/**
 * Состояние UI-данных: загрузка, успех или ошибка.
 */
sealed class UIState<T> {

    /** Состояние загрузки данных. */
    class Loading<T> : UIState<T>()

    /** Состояние успешной загрузки с данными. */
    data class Success<T>(val data: T) : UIState<T>()

    /** Состояние ошибки с исключением. */
    data class Failed<T>(val error: Throwable) : UIState<T>()

    companion object {

        @JvmStatic
                /** Создаёт состояние загрузки. */
        fun <T> loading() = Loading<T>()

        @JvmStatic
                /** Создаёт состояние успеха с данными. */
        fun <T> success(data: T) = Success(data)

        @JvmStatic
                /** Создаёт состояние ошибки с исключением. */
        fun <T> failed(error: Throwable) = Failed<T>(error)
    }
}

/**
 * Проверяет, является ли состояние успешным.
 */
fun UIState<*>.isSuccess(): Boolean {
    return this is UIState.Success
}

/**
 * Возвращает данные из [UIState.Success] или null для остальных состояний.
 */
fun <T> UIState<T>.getOrNull(): T? {
    if (this is UIState.Success) {
        return data
    }
    return null
}
