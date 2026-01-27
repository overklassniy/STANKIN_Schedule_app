package com.overklassniy.stankinschedule.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.overklassniy.stankinschedule.navigation.entry.BottomNavEntry

/**
 * Компонент нижней панели навигации (Bottom Navigation Bar).
 *
 * Отображает список разделов приложения и управляет навигацией между ними.
 * Подсвечивает активный раздел на основе текущего маршрута.
 *
 * @param navBackStackEntry Текущая запись стека навигации. Используется для определения текущего активного экрана.
 * @param navController Контроллер навигации для осуществления переходов.
 * @param screens Список элементов навигации ([BottomNavEntry]), которые должны быть отображены в меню.
 */
@Composable
fun AppNavigationBar(
    navBackStackEntry: NavBackStackEntry?,
    navController: NavController,
    screens: List<BottomNavEntry>,
) = NavigationBar {
    // Получаем текущий пункт назначения из стека
    val currentDestination = navBackStackEntry?.destination

    screens.forEach { screen ->

        // Определяем, выбран ли текущий элемент.
        // Используется startsWith, чтобы подсвечивать родительский раздел
        // даже если пользователь находится на вложенном экране (например, schedule/details).
        val isSelected by derivedStateOf {
            currentDestination?.route?.startsWith(screen.route) == true
        }

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(screen.iconRes),
                    contentDescription = null // Иконка декоративная, текст есть в label
                )
            },
            label = {
                Text(
                    text = stringResource(screen.nameRes),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            selected = isSelected,
            onClick = {
                navController.navigate(screen.route) {
                    // Настройка навигации при переключении вкладок

                    // Очищаем стек до стартового экрана графа (обычно "home"),
                    // чтобы избежать накопления огромного стека при переключении между вкладками
                    popUpTo(navController.graph.findStartDestination().id) {
                        // Сохраняем состояние экрана, с которого уходим,
                        // чтобы при возврате восстановить скролл, введенный текст и т.д.
                        // Логика saveState здесь также учитывает сброс стека при повторном нажатии на активную вкладку.
                        saveState = !isSelected || currentDestination?.route == screen.route
                    }
                    // Не создаем новую копию экрана, если он уже на вершине стека
                    launchSingleTop = true
                    // Восстанавливаем сохраненное состояние экрана при возврате на него
                    restoreState = true
                }
            }
        )
    }
}