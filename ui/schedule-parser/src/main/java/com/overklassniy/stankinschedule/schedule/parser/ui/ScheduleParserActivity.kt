package com.overklassniy.stankinschedule.schedule.parser.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.core.ui.components.LocalAnalytics
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleParserActivity : AppCompatActivity() {

    @Inject
    lateinit var loggerAnalytics: LoggerAnalytics

    private val viewModel: ScheduleParserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val filePath = intent.getStringExtra(EXTRA_FILE_PATH)
        val fileName = intent.getStringExtra(EXTRA_FILE_NAME)
        
        if (filePath != null && savedInstanceState == null) {
            viewModel.selectFileFromPath(filePath, fileName ?: "schedule")
        }

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalAnalytics provides loggerAnalytics) {
                    ScheduleParserScreen(
                        viewModel = viewModel,
                        onBackPressed = { onBackPressedDispatcher.onBackPressed() },
                        onImportSuccess = {
                            try {
                                val mainActivityClass = Class.forName("com.overklassniy.stankinschedule.MainActivity")
                                val intent = Intent(this@ScheduleParserActivity, mainActivityClass).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                }
                                startActivity(intent)
                                finish()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                onBackPressedDispatcher.onBackPressed()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    companion object {
        private const val EXTRA_FILE_PATH = "extra_file_path"
        private const val EXTRA_FILE_NAME = "extra_file_name"

        fun createIntent(context: Context, filePath: String, fileName: String): Intent {
            return Intent(context, ScheduleParserActivity::class.java).apply {
                putExtra(EXTRA_FILE_PATH, filePath)
                putExtra(EXTRA_FILE_NAME, fileName)
            }
        }
    }
}