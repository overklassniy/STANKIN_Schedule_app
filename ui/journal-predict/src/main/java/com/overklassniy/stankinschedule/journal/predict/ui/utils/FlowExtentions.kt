package com.overklassniy.stankinschedule.journal.predict.ui.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Комбинирует два StateFlow и возвращает новый StateFlow.
 *
 * @param flow1 Первый поток состояния.
 * @param flow2 Второй поток состояния.
 * @param scope Scope для stateIn.
 * @param sharingStarted Стратегия запуска (по умолчанию Eagerly).
 * @param transform Функция объединения значений.
 * @return Новый StateFlow с результатом трансформации.
 */
fun <T1, T2, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    scope: CoroutineScope,
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2) -> R,
): StateFlow<R> = combine(flow1, flow2) { o1, o2 ->
    transform.invoke(o1, o2)
}.stateIn(scope, sharingStarted, transform.invoke(flow1.value, flow2.value))