package com.overklassniy.stankinschedule.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import com.overklassniy.stankinschedule.core.domain.repository.ConsoleCrashReporter
import com.overklassniy.stankinschedule.core.domain.repository.ConsoleProductAnalytics
import com.overklassniy.stankinschedule.core.domain.repository.CrashReporter
import com.overklassniy.stankinschedule.core.domain.repository.ProductAnalytics


/**
 * CompositionLocal для доступа к реализации [ProductAnalytics] в UI.
 *
 * По умолчанию используется [ConsoleProductAnalytics].
 */
val LocalProductAnalytics = staticCompositionLocalOf<ProductAnalytics> {
    ConsoleProductAnalytics()
}

/**
 * CompositionLocal для доступа к реализации [CrashReporter] в UI.
 *
 * По умолчанию используется [ConsoleCrashReporter].
 */
val LocalCrashReporter = staticCompositionLocalOf<CrashReporter> {
    ConsoleCrashReporter()
}

/**
 * Отслеживает вход/выход на экран и отправляет события аналитики.
 *
 * @param screen Имя текущего экрана.
 */
@Composable
fun TrackCurrentScreen(screen: String) {
    val analytics = LocalProductAnalytics.current
    DisposableEffect(Unit) {
        analytics.logEvent(ProductAnalytics.SCREEN_ENTER, screen)
        onDispose {
            analytics.logEvent(ProductAnalytics.SCREEN_LEAVE, screen)
        }
    }
}
