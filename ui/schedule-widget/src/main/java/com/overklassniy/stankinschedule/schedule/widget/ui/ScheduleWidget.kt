package com.overklassniy.stankinschedule.schedule.widget.ui

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.overklassniy.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import com.overklassniy.stankinschedule.schedule.widget.ui.configure.ScheduleWidgetConfigureActivity
import com.overklassniy.stankinschedule.schedule.widget.ui.utils.ScheduleDeepLink
import com.overklassniy.stankinschedule.widget.data.repository.ScheduleWidgetPreferenceImpl
import com.overklassniy.stankinschedule.schedule.core.ui.R as R_core

/**
 * Утилитный объект для управления виджетом расписания.
 *
 * Содержит функции обновления и формирования интентов для элементов виджета.
 */
object ScheduleWidget {

    /**
     * Возвращает репозиторий настроек виджета.
     *
     * @param context Контекст приложения.
     * @return Репозиторий настроек.
     */
    fun widgetPreference(context: Context): ScheduleWidgetPreference =
        ScheduleWidgetPreferenceImpl(context)

    /**
     * Обновляет виджет по идентификатору расписания.
     *
     * @param context Контекст приложения.
     * @param scheduleId Идентификатор расписания.
     * @param fullUpdate Признак полной перерисовки списка.
     */
    fun updateWidgetById(context: Context, scheduleId: Long, fullUpdate: Boolean) {
        val widgetManager = AppWidgetManager.getInstance(context)
        val preference = widgetPreference(context)

        val ids = allScheduleWidgets(context, widgetManager)
        for (id in ids) {
            val data = preference.loadData(id)
            if (data != null && data.scheduleId == scheduleId) {
                onUpdateWidget(context, widgetManager, id, data, fullUpdate)
                return
            }
        }
    }

    /**
     * Обновляет все виджеты пользователя.
     *
     * @param context Контекст приложения.
     * @param fullUpdate Признак полной перерисовки списка.
     */
    fun updateAllWidgets(context: Context, fullUpdate: Boolean) {
        val widgetManager = AppWidgetManager.getInstance(context)
        val preference = widgetPreference(context)

        val ids = allScheduleWidgets(context, widgetManager)
        for (id in ids) {
            val data = preference.loadData(id)
            if (data != null) {
                onUpdateWidget(context, widgetManager, id, data, fullUpdate)
            }
        }
    }

    /**
     * Возвращает список идентификаторов всех виджетов расписания на устройстве.
     *
     * @param context Контекст приложения.
     * @param manager Менеджер виджетов AppWidgetManager.
     * @return Список идентификаторов виджетов.
     * Исключения: не выбрасывает.
     */
    private fun allScheduleWidgets(context: Context, manager: AppWidgetManager): List<Int> {
        val componentName = ComponentName(context, ScheduleWidgetProvider::class.java)
        return manager.getAppWidgetIds(componentName).toList()
    }

    /**
     * Выполняет обновление одного виджета.
     *
     * @param context Контекст приложения.
     * @param appWidgetManager Менеджер виджетов.
     * @param appWidgetId Идентификатор виджета.
     * @param appWidgetData Данные виджета или null.
     * @param fullUpdate Признак полной перерисовки списка.
     */
    fun onUpdateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        appWidgetData: ScheduleWidgetData?,
        fullUpdate: Boolean
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_schedule)

        // Обработка отсутствия данных
        if (appWidgetData == null) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        // установка имени
        val scheduleName = displayScheduleName(context, appWidgetData)
        views.setTextViewText(R.id.widget_schedule_name, scheduleName)
        val scheduleNameIntent = scheduleNameIntent(context, appWidgetData.scheduleId)
        views.setOnClickPendingIntent(R.id.widget_schedule_name, scheduleNameIntent)

        // конфигуратор
        val configureIntent = configureIntent(context, appWidgetId)
        views.setOnClickPendingIntent(R.id.widget_settings, configureIntent)

        // для открытия приложения на расписании на определенном дне
        val dayPendingIntent = dayPendingIntent(context, appWidgetId)
        views.setPendingIntentTemplate(R.id.widget_days, dayPendingIntent)

        // установка адаптера
        val dataIntent = remoteAdapterIntent(context, appWidgetId)
        @Suppress("DEPRECATION")
        views.setRemoteAdapter(R.id.widget_days, dataIntent)

        // Обновление виджета
        appWidgetManager.updateAppWidget(appWidgetId, views)

        if (fullUpdate) {
            @Suppress("DEPRECATION")
            appWidgetManager.notifyAppWidgetViewDataChanged(
                intArrayOf(appWidgetId),
                R.id.widget_days
            )
        }
    }

    /**
     * Создает PendingIntent для открытия экрана просмотра расписания из элемента дня.
     *
     * @param context Контекст приложения.
     * @param appWidgetId Идентификатор виджета для уникальности PendingIntent.
     * @return PendingIntent, совместимый с различными версиями Android.
     * Исключения: не выбрасывает.
     */
    private fun dayPendingIntent(context: Context, appWidgetId: Int): PendingIntent {
        return PendingIntent.getActivity(
            context,
            appWidgetId,
            Intent(ScheduleDeepLink.SCHEDULE_VIEWER_ACTION).setPackage(context.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE_COMPAT
        )
    }

    /**
     * Флаг совместимости для мутабельных PendingIntent на Android 12+.
     * Возвращает 0 на более старых версиях.
     */
    private val FLAG_MUTABLE_COMPAT: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0

    /**
     * Формирует отображаемое имя расписания с постфиксом подгруппы при необходимости.
     *
     * @param context Контекст приложения.
     * @param data Данные виджета: имя расписания, признак display и подгруппа.
     * @return Строка имени расписания, возможно с локализованным постфиксом подгруппы.
     * Примечания: если display = false или подгруппа COMMON, постфикс не добавляется.
     */
    private fun displayScheduleName(context: Context, data: ScheduleWidgetData): String {
        if (!data.display || data.subgroup == Subgroup.COMMON) return data.scheduleName

        val postfix = when (data.subgroup) {
            Subgroup.A -> context.getString(R_core.string.subgroup_a)
            Subgroup.B -> context.getString(R_core.string.subgroup_b)
            else -> "" // невозможно, т.к. выше сравнение с Subgroup.COMMON
        }

        return data.scheduleName + " " + postfix
    }


    /**
     * Интент для привязки адаптера списка дней к RemoteViews.
     * Для каждого виджета создается уникальный data Uri.
     *
     * @param context Контекст приложения.
     * @param appWidgetId Идентификатор виджета.
     * @return Интент для ScheduleWidgetService.
     */
    private fun remoteAdapterIntent(context: Context, appWidgetId: Int): Intent {
        val dataIntent = Intent(context, ScheduleWidgetService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }

        // уникальный адаптер для каждого виджета
        val data = dataIntent.toUri(Intent.URI_INTENT_SCHEME).toUri()
        dataIntent.data = data

        return dataIntent
    }

    /**
     * Создает PendingIntent для открытия экрана конфигурации виджета.
     *
     * @param context Контекст приложения.
     * @param appWidgetId Идентификатор виджета, передается в extras.
     * @return PendingIntent для запуска Activity конфигурации.
     * Исключения: не выбрасывает.
     */
    private fun configureIntent(context: Context, appWidgetId: Int): PendingIntent {
        return PendingIntent.getActivity(
            context,
            0,
            Intent(context, ScheduleWidgetConfigureActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Создает PendingIntent с TaskStackBuilder для открытия расписания по имени.
     * Сохраняет корректный back‑stack при навигации.
     *
     * @param context Контекст приложения.
     * @param scheduleId Идентификатор расписания.
     * @return PendingIntent для открытия экрана просмотра.
     * Исключения: не выбрасывает.
     */
    private fun scheduleNameIntent(context: Context, scheduleId: Long): PendingIntent {
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(ScheduleDeepLink.viewerIntent(scheduleId))
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }
}