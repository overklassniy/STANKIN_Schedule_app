package com.overklassniy.stankinschedule.journal.predict.ui.model

import com.overklassniy.stankinschedule.journal.core.domain.model.MarkType

class PredictMark(
    val discipline: String,
    val type: MarkType,
    val isExposed: Boolean,
    var value: Int,
)