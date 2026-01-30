package com.overklassniy.stankinschedule.schedule.editor.ui.components

import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel

/**
 * Состояние экрана редактора пары.
 *
 * Описывает жизненный цикл загрузки и применения изменений.
 * - Loading: данные загружаются
 * - Content: есть текущая пара (может быть null для создания)
 * - Complete: операция завершена
 */
sealed interface PairEditorState {

    object Loading : PairEditorState
    class Content(val pair: PairModel?) : PairEditorState
    object Complete : PairEditorState
}

/**
 * Возвращает модель пары из состояния Content, иначе null.
 *
 * @receiver Текущее состояние редактора пары.
 * @return Пара, если состояние Content, иначе null.
 */
fun PairEditorState.getOrNull(): PairModel? {
    return if (this is PairEditorState.Content) pair else null
}