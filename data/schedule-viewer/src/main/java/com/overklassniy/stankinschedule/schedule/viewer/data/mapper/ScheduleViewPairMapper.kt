package com.overklassniy.stankinschedule.schedule.viewer.data.mapper

import android.util.Patterns
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.CLASSROOM_ONLINE_PLACEHOLDER
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.LinkContent
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.TextContent
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ViewContent

/**
 * Преобразует модель пары [PairModel] в модель отображения [ScheduleViewPair].
 *
 * @return Объект [ScheduleViewPair] с отформатированными данными для UI.
 */
fun PairModel.toViewPair(): ScheduleViewPair {
    val classroomText = classroom.ifEmpty { CLASSROOM_ONLINE_PLACEHOLDER }
    return ScheduleViewPair(
        id = info.id,
        title = title,
        lecturer = lecturer,
        classroom = classroomViewContent(classroomText),
        subgroup = subgroup,
        type = type,
        startTime = time.startString(),
        endTime = time.endString(),
        link = link
    )
}

/**
 * Обрабатывает строку аудитории и создает соответствующий контент для отображения.
 *
 * Если в строке аудитории найдены ссылки, они извлекаются и форматируются.
 *
 * @param classroom Строка с названием аудитории или ссылкой.
 * @return Объект [ViewContent] (либо [TextContent], либо [LinkContent]).
 */
private fun classroomViewContent(classroom: String): ViewContent {

    var name = classroom
    val links = mutableListOf<LinkContent.Link>()

    val match = Patterns.WEB_URL.matcher(classroom)
    while (match.find()) {

        val url = match.group()
        // Используем 3-ю группу захвата (домен) или дефолтное имя, если группа не найдена
        val urlName = match.group(3) ?: "url name"
        val start = name.indexOf(url)

        name = name.replace(url, urlName)

        links.add(
            LinkContent.Link(start, urlName.length, url)
        )
    }

    if (links.isEmpty()) {
        return TextContent(classroom)
    }

    return LinkContent(
        name = name,
        links = links
    )
}