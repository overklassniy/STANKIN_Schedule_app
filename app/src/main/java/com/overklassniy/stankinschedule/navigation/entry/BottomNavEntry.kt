package com.overklassniy.stankinschedule.navigation.entry

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Базовый класс для элементов навигации, отображаемых в нижнем меню (Bottom Navigation Bar).
 *
 * Наследуется от [NavigationEntry] и расширяет его, добавляя ресурсы для визуального отображения
 * пункта меню (название и иконка).
 *
 * @property nameRes ID строкового ресурса для названия пункта меню (например, R.string.nav_home).
 *                   Должен быть валидным ресурсом строки (@StringRes).
 * @property iconRes ID ресурса изображения для иконки пункта меню (например, R.drawable.ic_home).
 *                   Должен быть валидным ресурсом drawable (@DrawableRes).
 * @param route Уникальный строковый маршрут для навигации. Передается в конструктор родительского класса [NavigationEntry].
 */
abstract class BottomNavEntry(
    route: String,
    @get:StringRes val nameRes: Int,
    @get:DrawableRes val iconRes: Int
) : NavigationEntry(route)