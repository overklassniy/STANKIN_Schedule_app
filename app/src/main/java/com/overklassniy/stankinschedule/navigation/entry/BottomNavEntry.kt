package com.overklassniy.stankinschedule.navigation.entry

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Запись навигации для нижней панели навигации.
 *
 * @param route Маршрут для навигации
 * @param nameRes Ресурс строки с названием
 * @param iconRes Ресурс иконки
 * @param hierarchy Иерархия маршрутов для определения активного экрана
 */
abstract class BottomNavEntry(
    route: String,
    @get:StringRes val nameRes: Int,
    @get:DrawableRes val iconRes: Int,
    val hierarchy: List<String> = listOf(route)
) : NavigationEntry(route)