package com.overklassniy.stankinschedule.schedule.widget.ui.utils

import android.content.Intent
import androidx.core.net.toUri
import org.joda.time.LocalDate

/**
 * Утилиты для построения deep‑link Intent'ов виджета.
 *
 * Предоставляет действие запуска экрана просмотра и шаблон deep‑link.
 */
object ScheduleDeepLink {

    /** Действие для запуска экрана просмотра расписания. */
    const val SCHEDULE_VIEWER_ACTION: String =
        "com.overklassniy.stankinschedule.action.SCHEDULE_VIEWER"

    /** Шаблон deep‑link для навигации по расписанию. */
    const val DEEP_LINK = "app://stankinschedule.com/schedule/viewer/{scheduleId}?date={startDate}"

    /**
     * Создает Intent для открытия экрана просмотра расписания.
     *
     * @param id Идентификатор расписания.
     * @param date Начальная дата или null.
     * @return Intent с действием и uri, пригодный для навигации.
     */
    fun viewerIntent(id: Long, date: LocalDate? = null): Intent = Intent(
        SCHEDULE_VIEWER_ACTION,
        if (date == null) {
            "app://stankinschedule.com/schedule/viewer/$id"
        } else {
            "app://stankinschedule.com/schedule/viewer/$id?date=${date.toString("yyyy-MM-dd")}"
        }.toUri(),
    ).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
    }
}