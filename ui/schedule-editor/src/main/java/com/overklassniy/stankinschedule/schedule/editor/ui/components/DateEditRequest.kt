package com.overklassniy.stankinschedule.schedule.editor.ui.components

import com.overklassniy.stankinschedule.schedule.core.domain.model.DateItem

sealed interface DateEditorRequest {
    class Edit(val date: DateItem) : DateEditorRequest
    object New : DateEditorRequest
}