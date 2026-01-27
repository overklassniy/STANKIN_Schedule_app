package com.overklassniy.stankinschedule.navigation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.overklassniy.stankinschedule.R
import com.overklassniy.stankinschedule.navigation.entry.BottomNavEntry
import com.overklassniy.stankinschedule.navigation.entry.DestinationNavEntry
import com.overklassniy.stankinschedule.schedule.creator.ui.ScheduleCreatorSheet
import com.overklassniy.stankinschedule.schedule.editor.ui.PairEditorActivity
import com.overklassniy.stankinschedule.schedule.list.ui.ScheduleScreen
import com.overklassniy.stankinschedule.schedule.parser.ui.ScheduleParserActivity
import com.overklassniy.stankinschedule.schedule.repository.ui.ScheduleRepositoryActivity
import com.overklassniy.stankinschedule.schedule.table.ui.ScheduleTableViewActivity
import com.overklassniy.stankinschedule.schedule.viewer.ui.ScheduleViewerScreen
import com.overklassniy.stankinschedule.schedule.widget.ui.utils.ScheduleDeepLink
import kotlinx.coroutines.launch

/**
 * Элемент навигации для экрана "Расписание" в нижнем меню приложения.
 *
 * Является корневым экраном раздела расписания.
 * Определяет маршрут, название, иконку и список дочерних маршрутов для подсветки активного пункта меню.
 */
object ScheduleNavEntry : BottomNavEntry(
    route = "schedule",
    nameRes = R.string.nav_schedule,
    iconRes = R.drawable.nav_schedule
)

/**
 * Элемент навигации для экрана просмотра конкретного расписания.
 *
 * Поддерживает передачу аргументов:
 * - scheduleId: Идентификатор расписания (обязательный, Long).
 * - startDate: Дата начала просмотра (опциональный, String).
 *
 * Также определяет методы для парсинга этих аргументов из [NavBackStackEntry].
 */
object ScheduleViewerNavEntry : DestinationNavEntry(
    route = "schedule/{scheduleId}?date={startDate}",
    arguments = listOf(
        navArgument(name = "scheduleId") { type = NavType.LongType },
        navArgument(name = "startDate") { type = NavType.StringType; nullable = true }
    )
) {
    /**
     * Создает маршрут с подставленным идентификатором расписания.
     *
     * @param id Идентификатор расписания.
     * @return Строка маршрута для навигации.
     */
    fun routeWithArgs(id: Long): String = "schedule/$id"

    /**
     * Извлекает идентификатор расписания из аргументов навигации.
     *
     * @param entry Запись в стеке навигации.
     * @return Идентификатор расписания или -1, если аргумент отсутствует.
     */
    fun parseScheduleId(entry: NavBackStackEntry) = entry.arguments?.getLong("scheduleId") ?: -1

    /**
     * Извлекает дату начала из аргументов навигации.
     *
     * @param entry Запись в стеке навигации.
     * @return Строка с датой или null.
     */
    fun parseStartDate(entry: NavBackStackEntry) = entry.arguments?.getString("startDate")
}

/**
 * Настраивает граф навигации для раздела "Расписание".
 *
 * Добавляет в граф следующие экраны:
 * 1. Список расписаний ([ScheduleScreen]) - стартовый экран.
 * 2. Просмотр расписания ([ScheduleViewerScreen]) - с поддержкой deep links.
 * 3. Создание расписания ([ScheduleCreatorSheet]) - в виде bottom sheet (локально).
 *
 * @receiver [NavGraphBuilder] Строитель графа навигации.
 * @param navController [NavController] Контроллер навигации для управления переходами.
 * @param showSnackBarState Callback для отображения всплывающих уведомлений (Snackbar).
 */
@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.schedule(
    navController: NavController,
    showSnackBarState: (message: String) -> Unit
) {
    // Экран списка расписаний (Главная страница раздела)
    composable(route = ScheduleNavEntry.route) {
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        val (showCreatorSheet, setShowCreatorSheet) = remember { mutableStateOf(false) }

        ScheduleScreen(
            // Переход к созданию нового расписания (открытие шторки)
            onScheduleCreate = {
                setShowCreatorSheet(true)
            },
            // Переход к просмотру выбранного расписания
            onScheduleClicked = { id ->
                navController.navigate(route = ScheduleViewerNavEntry.routeWithArgs(id))
            },
            viewModel = hiltViewModel(),
            modifier = Modifier.fillMaxSize()
        )

        if (showCreatorSheet) {
            val context = LocalContext.current

            ModalBottomSheet(
                onDismissRequest = { setShowCreatorSheet(false) },
                sheetState = sheetState
            ) {
                ScheduleCreatorSheet(
                    onNavigateBack = {
                        scope.launch {
                            sheetState.hide()
                            setShowCreatorSheet(false)
                        }
                    },
                    // Переход к репозиторию готовых расписаний (отдельная Activity)
                    onRepositoryClicked = {
                        context.startActivity(
                            Intent(
                                context,
                                ScheduleRepositoryActivity::class.java
                            )
                        )
                        setShowCreatorSheet(false)
                    },
                    // Переход к импорту расписания (парсинг) (отдельная Activity)
                    onImportClicked = {
                        context.startActivity(Intent(context, ScheduleParserActivity::class.java))
                        setShowCreatorSheet(false)
                    },
                    onShowSnackBar = showSnackBarState,
                    viewModel = hiltViewModel(),
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    }

    // Экран просмотра конкретного расписания
    composable(
        route = ScheduleViewerNavEntry.route,
        arguments = ScheduleViewerNavEntry.arguments,
        deepLinks = listOf(navDeepLink {
            uriPattern = ScheduleDeepLink.DEEP_LINK
        }) // Поддержка открытия по ссылке
    ) { backStackEntry ->
        val context = LocalContext.current

        ScheduleViewerScreen(
            scheduleId = ScheduleViewerNavEntry.parseScheduleId(backStackEntry),
            startDate = ScheduleViewerNavEntry.parseStartDate(backStackEntry),
            scheduleName = null,
            viewModel = hiltViewModel(),
            onBackPressed = { navController.navigateUp() },
            // Открытие редактора пар (отдельная Activity)
            onEditorClicked = { scheduleId, pairId ->
                context.startActivity(
                    PairEditorActivity.createIntent(context, scheduleId, pairId)
                )
            },
            // Открытие табличного просмотра расписания (отдельная Activity)
            onTableViewClicked = { scheduleId ->
                context.startActivity(
                    ScheduleTableViewActivity.createIntent(context, scheduleId)
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}