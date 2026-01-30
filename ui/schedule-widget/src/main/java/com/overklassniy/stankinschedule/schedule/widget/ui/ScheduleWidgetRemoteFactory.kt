package com.overklassniy.stankinschedule.schedule.widget.ui

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.overklassniy.stankinschedule.core.ui.ext.toTitleCase
import com.overklassniy.stankinschedule.schedule.core.domain.model.Subgroup
import com.overklassniy.stankinschedule.schedule.core.domain.model.Type
import com.overklassniy.stankinschedule.schedule.core.ui.PairColors
import com.overklassniy.stankinschedule.schedule.core.ui.toColor
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetDay
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetPair
import com.overklassniy.stankinschedule.schedule.widget.domain.model.ScheduleWidgetPairType
import com.overklassniy.stankinschedule.schedule.widget.domain.usecase.ScheduleWidgetUseCase
import com.overklassniy.stankinschedule.schedule.widget.ui.base.CoroutinesRemoteFactory
import com.overklassniy.stankinschedule.schedule.widget.ui.utils.ScheduleDeepLink
import org.joda.time.LocalDate
import com.overklassniy.stankinschedule.core.ui.R as R_core

class ScheduleWidgetRemoteFactory(
    context: Context,
    intent: Intent,
    private val useCase: ScheduleWidgetUseCase
) : CoroutinesRemoteFactory(context, intent) {

    private var isError = false

    private var scheduleId: Long = -1

    private var days: List<ScheduleWidgetDay> = emptyList()

    private var colors: PairColors = PairColorGroup.default().toColor()

    private val lightColor: Int =
        context.resources.getColor(R_core.color.md_theme_light_onSurface, context.theme)

    private val darkColor: Int =
        context.resources.getColor(R_core.color.md_theme_dark_onSurface, context.theme)

    /**
     * Загружает данные виджета и преобразует их в список дней для отображения.
     *
     * Алгоритм:
     * 1. Получить сохраненные настройки виджета по appWidgetId.
     * 2. Если данные отсутствуют — установить isError = true и завершить метод.
     * 3. Определить текущую дату и диапазон: сегодня + 7 дней.
     * 4. Запросить пары по дням для указанной подгруппы в выбранном диапазоне.
     * 5. Сформировать модели ScheduleWidgetDay и ScheduleWidgetPair и обновить состояние days.
     * 6. Загрузить и применить набор цветов для типов пар.
     *
     * Поведение при ошибке:
     * - Устанавливается флаг isError и список дней не заполняется.
     *
     * Возвращаемое значение:
     * - Unit: метод изменяет внутреннее состояние фабрики без возврата значения.
     */
    override suspend fun onDataChanged() {
        val data = useCase.loadWidgetData(appWidgetId)

        if (data == null) {
            isError = true
            return
        }

        scheduleId = data.scheduleId

        val today = LocalDate.now()
        val daysWithPairs = useCase.scheduleDays(
            data.scheduleId, data.subgroup, today, today.plusDays(7)
        )

        days = daysWithPairs.mapIndexed { index, pairs ->
            val now = today.plusDays(index)

            ScheduleWidgetDay(
                day = now.toString("EE, dd MMMM").toTitleCase(),
                date = now,
                pairs = pairs.map { pair ->
                    ScheduleWidgetPair(
                        title = pair.title,
                        classroom = pair.classroom,
                        time = pair.time.toString(),
                        type = when (pair.type) {
                            Type.LECTURE -> ScheduleWidgetPairType.Lecture
                            Type.SEMINAR -> ScheduleWidgetPairType.Seminar
                            Type.LABORATORY -> {
                                when (pair.subgroup) {
                                    Subgroup.A -> ScheduleWidgetPairType.SubgroupA
                                    Subgroup.B -> ScheduleWidgetPairType.SubgroupB
                                    else -> ScheduleWidgetPairType.Laboratory
                                }
                            }
                        }
                    )
                }
            )
        }

        colors = useCase.pairColors().toColor()
    }

    /**
     * Возвращает количество элементов списка виджета.
     *
     * Примечания:
     * - В режиме ошибки возвращает 1, чтобы отобразить макет ошибки.
     *
     * Возвращаемое значение:
     * - Int: число элементов для отображения.
     */
    override fun getCount(): Int = if (isError) 1 else days.size

    /**
     * Возвращает RemoteViews для указанной позиции списка.
     *
     * @param position Индекс элемента.
     * @return RemoteViews макет ошибки при isError=true, иначе макет дня с парами.
     */
    override fun getViewAt(position: Int): RemoteViews {
        return if (isError) getErrorView() else getDayView(days[position])
    }

    /**
     * Создает RemoteViews для состояния ошибки загрузки данных.
     *
     * Возвращаемое значение:
     * - RemoteViews: макет widget_schedule_error.
     */
    private fun getErrorView(): RemoteViews {
        return RemoteViews(packageName, R.layout.widget_schedule_error)
    }

    /**
     * Создает RemoteViews для одного дня, включая заголовок и список пар.
     *
     * @param day Модель дня с локализованным заголовком, датой и списком пар.
     * @return RemoteViews собранного элемента дня для списка виджета.
     */
    private fun getDayView(day: ScheduleWidgetDay): RemoteViews {
        val dayView = RemoteViews(packageName, R.layout.widget_schedule_item)
        dayView.removeAllViews(R.id.day_layout)

        // Заголовок дня
        dayView.setTextViewText(R.id.day_title, day.day)

        if (day.pairs.isEmpty()) {
            val emptyDay = RemoteViews(packageName, R.layout.widget_schedule_item_empty)
            dayView.addView(R.id.day_layout, emptyDay)
        } else {
            addPairs(day.pairs) { pairsView, color ->
                setPairColor(pairsView, color)
                dayView.addView(R.id.day_layout, pairsView)
            }
        }

        setDayClickIntent(dayView, day.date)

        return dayView
    }

    /**
     * Устанавливает интент для клика по дню и передает дату в экран просмотра расписания (deep-link).
     *
     * @param view Контейнер RemoteViews для дня.
     * @param date Дата дня, которая будет передана в навигационный интент.
     */
    private fun setDayClickIntent(view: RemoteViews, date: LocalDate) {
        view.setOnClickFillInIntent(
            R.id.widget_day, ScheduleDeepLink.viewerIntent(scheduleId, date)
        )
    }

    /**
     * Устанавливает цвет оформления элемента пары с учетом версии API.
     * На API 31+ применяет фон через ColorStateList, на более ранних — цвет фильтра для ImageView.
     *
     * @param view RemoteViews элемента пары.
     * @param color Цвет ARGB для оформления.
     */
    private fun setPairColor(view: RemoteViews, color: Int) {
        // С API 31+ используем установку ColorStateList, иначе — setColorFilter на ImageView.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setColorStateList(
                R.id.widget_pair,
                "setBackgroundTintList",
                ColorStateList.valueOf(color)
            )
        } else {
            view.setInt(
                R.id.widget_pair_type,
                "setColorFilter",
                color
            )
        }
    }

    /**
     * Добавляет элементы пар в контейнер дня.
     *
     * @param pairs Список пар.
     * @param addView Колбэк добавления элемента с применением цвета.
     */
    private fun addPairs(
        pairs: List<ScheduleWidgetPair>,
        addView: (view: RemoteViews, color: Int) -> Unit
    ) {
        for (pair in pairs) {
            val pairsView = RemoteViews(packageName, R.layout.widget_schedule_item_pair)
            val (color, isDark) = colorForPair(pair.type)

            pairsView.setTextViewText(R.id.widget_pair_title, pair.title)
            val timeWithClassroom = if (pair.classroom.isNotEmpty()) {
                pair.time + ", " + pair.classroom
            } else {
                pair.time
            }

            pairsView.setTextViewText(R.id.widget_pair_time, timeWithClassroom)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pairsView.setTextColor(
                    R.id.widget_pair_title,
                    if (isDark) darkColor else lightColor
                )
                pairsView.setTextColor(R.id.widget_pair_time, if (isDark) darkColor else lightColor)
            }

            addView(pairsView, color)
        }
    }

    /**
     * Определяет цвет ARGB и признак темного фона для указанного типа пары.
     *
     * Критерий темного фона:
     * - Считается темным, если яркость (luminance) меньше 0.5.
     *
     * @param type Тип пары для выбора цвета из набора.
     * @return Pair<Int, Boolean> цвет в ARGB и флаг темного фона.
     */
    private fun colorForPair(type: ScheduleWidgetPairType): Pair<Int, Boolean> {
        val color = when (type) {
            ScheduleWidgetPairType.Lecture -> colors.lectureColor
            ScheduleWidgetPairType.Seminar -> colors.seminarColor
            ScheduleWidgetPairType.Laboratory -> colors.laboratoryColor
            ScheduleWidgetPairType.SubgroupA -> colors.subgroupAColor
            ScheduleWidgetPairType.SubgroupB -> colors.subgroupBColor
        }.toArgb()

        return color to (ColorUtils.calculateLuminance(color) < 0.5)
    }

    /**
     * Возвращает представление состояния загрузки для элементов виджета.
     *
     * Возвращаемое значение:
     * - RemoteViews с макетом widget_schedule_item_loading.
     */
    override fun getLoadingView(): RemoteViews {
        return RemoteViews(packageName, R.layout.widget_schedule_item_loading)
    }
}