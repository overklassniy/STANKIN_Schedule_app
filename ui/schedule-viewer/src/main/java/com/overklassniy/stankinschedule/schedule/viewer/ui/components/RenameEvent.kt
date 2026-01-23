package com.overklassniy.stankinschedule.schedule.viewer.ui.components

sealed interface RenameEvent {
    object Rename : RenameEvent
    object Cancel : RenameEvent
}