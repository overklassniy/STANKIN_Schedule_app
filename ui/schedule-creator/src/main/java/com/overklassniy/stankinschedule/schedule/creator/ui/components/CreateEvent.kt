package com.overklassniy.stankinschedule.schedule.creator.ui.components

sealed interface CreateEvent {
    object Cancel : CreateEvent
    object New : CreateEvent
}