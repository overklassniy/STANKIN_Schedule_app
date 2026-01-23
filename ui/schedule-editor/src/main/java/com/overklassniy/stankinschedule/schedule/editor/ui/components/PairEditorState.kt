package com.overklassniy.stankinschedule.schedule.editor.ui.components

import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel

sealed interface PairEditorState {

    object Loading : PairEditorState
    class Content(val pair: PairModel?) : PairEditorState
    class Error(val t: Throwable) : PairEditorState
    object Complete : PairEditorState
}

fun PairEditorState.getOrNull(): PairModel? {
    return if (this is PairEditorState.Content) pair else null
}