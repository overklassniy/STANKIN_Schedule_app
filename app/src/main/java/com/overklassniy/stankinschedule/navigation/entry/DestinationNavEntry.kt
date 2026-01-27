package com.overklassniy.stankinschedule.navigation.entry

import androidx.navigation.NamedNavArgument

/**
 * Базовый класс для пунктов назначения навигации, которые могут принимать аргументы.
 *
 * Используется для определения экранов, которые не обязательно присутствуют в нижнем меню,
 * но являются частью графа навигации (например, детальные экраны, экраны с параметрами).
 *
 * @property arguments Список именованных аргументов навигации ([NamedNavArgument]),
 *                     которые ожидает данный маршрут. По умолчанию пустой список.
 * @param route Уникальный строковый маршрут для навигации. Передается в конструктор [NavigationEntry].
 */
abstract class DestinationNavEntry(
    route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) : NavigationEntry(route)