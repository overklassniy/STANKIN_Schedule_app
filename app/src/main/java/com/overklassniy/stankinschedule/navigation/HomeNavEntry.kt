package com.overklassniy.stankinschedule.navigation

import com.overklassniy.stankinschedule.R
import com.overklassniy.stankinschedule.navigation.entry.BottomNavEntry

/**
 * Запись навигации для главного экрана приложения.
 *
 * Используется нижней панелью навигации для отображения пункта "Главная".
 */
object HomeNavEntry : BottomNavEntry(
    route = "home", nameRes = R.string.nav_home, iconRes = R.drawable.nav_home
)
