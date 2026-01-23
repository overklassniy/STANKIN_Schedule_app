package com.overklassniy.stankinschedule.schedule.parser.ui.model

import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParseResult
import com.overklassniy.stankinschedule.schedule.table.domain.model.ScheduleTable

class ParsedFile(
    val successResult: List<ParseResult.Success>,
    val missingResult: List<ParseResult.Missing>,
    val errorResult: List<ParseResult.Error>,
    val table: ScheduleTable
)