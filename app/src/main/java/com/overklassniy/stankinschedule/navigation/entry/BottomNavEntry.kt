package com.overklassniy.stankinschedule.navigation.entry

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

abstract class BottomNavEntry(
    route: String,
    @get:StringRes val nameRes: Int,
    @get:DrawableRes val iconRes: Int,
    val hierarchy: List<String> = listOf(route)
) : NavigationEntry(route)