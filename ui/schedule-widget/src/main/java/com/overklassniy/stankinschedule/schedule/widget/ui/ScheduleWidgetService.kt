package com.overklassniy.stankinschedule.schedule.widget.ui

import android.content.Intent
import android.widget.RemoteViewsService
import com.overklassniy.stankinschedule.schedule.widget.domain.usecase.ScheduleWidgetUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Сервис, предоставляющий фабрику RemoteViews для виджета расписания.
 *
 * Интегрирован с Hilt для получения UseCase.
 */
@AndroidEntryPoint
class ScheduleWidgetService : RemoteViewsService() {

    @Inject
    lateinit var useCase: ScheduleWidgetUseCase

    /**
     * Возвращает фабрику RemoteViews для указанного Intent.
     *
     * @param intent Интент запуска фабрики.
     * @return Фабрика ScheduleWidgetRemoteFactory.
     */
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ScheduleWidgetRemoteFactory(
            context = applicationContext,
            intent = intent,
            useCase = useCase
        )
    }
}