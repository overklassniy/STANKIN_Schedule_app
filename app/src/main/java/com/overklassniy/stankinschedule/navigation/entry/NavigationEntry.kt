package com.overklassniy.stankinschedule.navigation.entry

/**
 * Базовый абстрактный класс для всех записей навигации в приложении.
 *
 * Определяет минимальный контракт для любой точки назначения — наличие маршрута ([route]).
 * От этого класса наследуются более специфичные типы записей, такие как [BottomNavEntry] и [DestinationNavEntry].
 *
 * @property route Уникальный строковый идентификатор маршрута (URI pattern).
 *                 Используется Navigation Component для сопоставления URL с Composable экраном.
 *                 Может содержать параметры (например, "schedule/{id}").
 */
abstract class NavigationEntry(
    val route: String,
)