package com.overklassniy.stankinschedule.navigation.entry

import androidx.navigation.NamedNavArgument

/**
 * Запись навигации для экрана назначения с аргументами.
 *
 * @param route Маршрут для навигации
 * @param arguments Список аргументов навигации
 */
abstract class DestinationNavEntry(
    route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) : NavigationEntry(route)