package com.overklassniy.stankinschedule.settings.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.overklassniy.stankinschedule.core.domain.repository.ProductAnalytics
import com.overklassniy.stankinschedule.core.domain.settings.ThemePreset
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    @Inject
    lateinit var productAnalytics: ProductAnalytics

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                val navController = rememberNavController()

                // Применение пресета темы к системному режиму (status bar, navigation bar)
                val themePreset by viewModel.themePreset.collectAsState()
                LaunchedEffect(themePreset) {
                    val current = themePreset ?: return@LaunchedEffect
                    val mode = when (current) {
                        ThemePreset.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        ThemePreset.Dark -> AppCompatDelegate.MODE_NIGHT_YES
                        ThemePreset.Light -> AppCompatDelegate.MODE_NIGHT_NO
                        ThemePreset.Custom -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }

                    if (mode != AppCompatDelegate.getDefaultNightMode()) {
                        AppCompatDelegate.setDefaultNightMode(mode)
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "root"
                ) {
                    composable(route = "root") {
                        RootSettingsScreen(
                            viewModel = viewModel,
                            onBackPressed = { onBackPressedDispatcher.onBackPressed() },
                            navigateToSchedule = { navController.navigate("schedule") },
                            navigateToMore = { navController.navigate("more") },
                            navigateToTheme = { navController.navigate("theme") },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable(route = "theme") {
                        ThemeSettingsScreen(
                            viewModel = viewModel,
                            onBackPressed = { navController.navigateUp() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable(route = "schedule") {
                        ScheduleSettingsScreen(
                            viewModel = viewModel,
                            onBackPressed = { navController.navigateUp() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable(route = "more") {
                        MoreSettingsScreen(
                            viewModel = viewModel,
                            onBackPressed = { navController.navigateUp() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        productAnalytics.logEvent(ProductAnalytics.SCREEN_ENTER, "SettingsActivity")
    }

    override fun onDestroy() {
        super.onDestroy()
        productAnalytics.logEvent(ProductAnalytics.SCREEN_LEAVE, "SettingsActivity")
    }
}
