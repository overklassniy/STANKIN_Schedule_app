package com.overklassniy.stankinschedule.schedule.editor.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.datepicker.MaterialDatePicker
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import com.overklassniy.stankinschedule.schedule.editor.ui.components.DateRequest
import com.overklassniy.stankinschedule.schedule.editor.ui.components.DateResult
import com.overklassniy.stankinschedule.schedule.editor.ui.components.EditorMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.joda.time.LocalDate

@AndroidEntryPoint
/**
 * Экран редактора пары.
 *
 * Инициализирует Compose UI, получает аргументы из Intent, обрабатывает запросы на выбор даты.
 */
class PairEditorActivity : AppCompatActivity() {

    private val viewModel: PairEditorViewModel by viewModels()

    /**
     * Инициализация активити редактора пары.
     *
     * Алгоритм:
     * 1. Отключает системные отступы.
     * 2. Читает аргументы пары и расписания.
     * 3. Строит Compose UI и подписывается на запросы выбора даты.
     *
     * @param savedInstanceState Сохранённое состояние.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Отключаем системные отступы, чтобы UI занимал всю область экрана
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Получение ID пары и расписания из Intent
        val scheduleId: Long = intent.getLongExtra(SCHEDULE_ID, -1L)
        var pairId: Long? = intent.getLongExtra(PAIR_ID, -1L)
        if (pairId == -1L) pairId = null

        setContent {
            AppTheme {
                PairEditorScreen(
                    mode = if (pairId == null) EditorMode.Create else EditorMode.Edit,
                    scheduleId = scheduleId,
                    pairId = pairId,
                    onBackClicked = {
                        onBackPressedDispatcher.onBackPressed()
                    },
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pickerRequests.collectLatest {
                    showDatePicker(it)
                }
            }
        }
    }

    /**
     * Показывает диалог выбора даты.
     *
     * @param request Запрос на выбор даты с текущим значением и заголовком.
     */
    private fun showDatePicker(request: DateRequest) {
        // Строим диалог выбора даты
        val dialog = MaterialDatePicker.Builder.datePicker()
            .setTitleText(request.title)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            // Преобразуем LocalDate в миллисекунды, привязанные к текущему времени
            .setSelection(request.selectedDate.toDateTimeAtCurrentTime().millis)
            .build()

        // При подтверждении выбора отправляем результат во ViewModel
        dialog.addOnPositiveButtonClickListener {
            viewModel.onDateResult(DateResult(request.id, LocalDate(it)))
        }

        dialog.show(supportFragmentManager, DATE_PICKER_TAG)
    }

    /**
     * Константы и фабрика Intent для запуска PairEditorActivity.
     */
    companion object {
        /** Тег для диалога выбора даты. */
        private const val DATE_PICKER_TAG = "date_picker_tag"

        /** Ключ extra для идентификатора пары. */
        private const val PAIR_ID = "pair_id"

        /** Ключ extra для идентификатора расписания. */
        private const val SCHEDULE_ID = "schedule_id"

        /**
         * Создаёт Intent для запуска экрана редактора пары.
         *
         * @param context Контекст.
         * @param scheduleId Идентификатор расписания.
         * @param pairId Идентификатор пары или null для создания новой.
         * @return Готовый Intent.
         */
        fun createIntent(context: Context, scheduleId: Long, pairId: Long?): Intent {
            return Intent(context, PairEditorActivity::class.java).apply {
                putExtra(PAIR_ID, pairId)
                putExtra(SCHEDULE_ID, scheduleId)
            }
        }
    }
}