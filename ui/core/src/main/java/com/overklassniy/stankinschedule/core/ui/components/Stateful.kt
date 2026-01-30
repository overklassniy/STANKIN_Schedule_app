package com.overklassniy.stankinschedule.core.ui.components

import androidx.compose.runtime.Composable

/**
 * Рендерит содержимое по состоянию [UIState] (успех/загрузка/ошибка).
 *
 * @param state Состояние UI.
 * @param onSuccess Контент при успехе.
 * @param onLoading Контент при загрузке.
 * @param onFailed Контент при ошибке.
 */
@Composable
inline fun <T : Any> Stateful(
    state: UIState<T>,
    onSuccess: @Composable (data: T) -> Unit,
    onLoading: @Composable () -> Unit,
    onFailed: @Composable (error: Throwable) -> Unit,
) {
    when (state) {
        is UIState.Success -> onSuccess(state.data)
        is UIState.Failed -> onFailed(state.error)
        is UIState.Loading -> onLoading()
    }
}

/**
 * Упрощённая версия [Stateful] без обработки ошибки.
 *
 * @param state Состояние UI.
 * @param onSuccess Контент при успехе.
 * @param onLoading Контент при загрузке.
 */
@Composable
inline fun <T : Any?> Stateful(
    state: UIState<T>,
    onSuccess: @Composable (data: T) -> Unit,
    onLoading: @Composable () -> Unit,
) {
    when (state) {
        is UIState.Success -> onSuccess(state.data)
        else -> onLoading()
    }
}