package com.overklassniy.stankinschedule.schedule.table.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.overklassniy.stankinschedule.schedule.table.domain.model.ScheduleTable
import com.overklassniy.stankinschedule.schedule.table.domain.model.TableConfig
import com.overklassniy.stankinschedule.schedule.table.domain.model.TableMode
import com.overklassniy.stankinschedule.schedule.table.domain.usecase.AndroidTableUseCase
import com.overklassniy.stankinschedule.schedule.table.ui.components.ExportFormat
import com.overklassniy.stankinschedule.schedule.table.ui.components.ExportProgress
import com.overklassniy.stankinschedule.schedule.table.ui.components.ExportType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel экрана таблицы расписания.
 *
 * Назначение: загружает данные расписания, собирает модель таблицы
 * и управляет процессом экспорта в изображение или PDF.
 * Содержит состояния для имени расписания, конфигурации таблицы и прогресса экспорта.
 */
@HiltViewModel
class ScheduleTableViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase,
    private val tableUseCase: AndroidTableUseCase,
    private val loggerAnalytics: LoggerAnalytics
) : ViewModel() {

    private val _scheduleName = MutableStateFlow("")

    /**
     * Публичный поток имени расписания.
     * Возвращает пустую строку, если модель расписания еще не загружена.
     */
    val scheduleName = _scheduleName.asStateFlow()

    private val _schedule = MutableStateFlow<ScheduleModel?>(null)

    private val _tableConfig = MutableStateFlow(TableConfig.default())

    /**
     * Публичное состояние конфигурации таблицы.
     * Содержит цвет, размер длинной стороны экрана, режим и номер страницы.
     */
    val tableConfig = _tableConfig.asStateFlow()

    private val _table = MutableStateFlow<ScheduleTable?>(null)

    /**
     * Публичное состояние модели таблицы для отрисовки.
     * Значение null означает, что таблица пересобирается или еще не готова.
     */
    val table = _table.asStateFlow()


    private val _exportProgress = MutableStateFlow<ExportProgress>(ExportProgress.Nothing)

    /**
     * Публичное состояние прогресса экспорта.
     * Возможные состояния: Nothing, Running, Finished, Error.
     */
    val exportProgress = _exportProgress.asStateFlow()

    private var exportJob: Job? = null
    private var saveFormat: ExportFormat? = null


    init {
        loadTable()
    }

    /**
     * Пересобирает модель таблицы при изменении конфигурации или расписания.
     *
     * Алгоритм:
     * 1. Комбинирует конфигурацию и модель расписания.
     * 2. На время пересборки устанавливает table = null для показа индикатора.
     * 3. Создает ScheduleTable в зависимости от режима и номера страницы.
     *
     * @return Unit
     * Исключения: возможна CancellationException при отмене корутины, наружу не пробрасывается.
     */
    private fun loadTable() {
        viewModelScope.launch {
            _tableConfig.combine(_schedule.filterNotNull()) { t1, t2 -> t1 to t2 }
                .collectLatest { (config, schedule) ->
                    _table.value = null

                    val table = when (config.mode) {
                        TableMode.Full -> ScheduleTable(
                            schedule = schedule
                        )

                        TableMode.Weekly -> ScheduleTable(
                            schedule = schedule,
                            date = LocalDate.now().plusDays(config.page * 7)
                        )
                    }

                    _table.value = table
                }
        }
    }

    /**
     * Загружает модель расписания по идентификатору.
     *
     * @param scheduleId Идентификатор расписания.
     */
    fun loadSchedule(scheduleId: Long) {
        viewModelScope.launch {
            scheduleUseCase.scheduleModel(scheduleId)
                .collectLatest { schedule ->
                    _schedule.value = schedule
                    _scheduleName.value = schedule?.info?.scheduleName ?: ""
                }
        }
    }

    /**
     * Устанавливает конфигурацию таблицы.
     *
     * @param color Цвет отрисовки в формате ARGB Int.
     * @param longScreenSize Длина большей стороны экрана в пикселях.
     * @param mode Режим таблицы: полный или недельный.
     * @param pageNumber Номер страницы для недельного режима.
     */
    fun setConfig(color: Int, longScreenSize: Float, mode: TableMode, pageNumber: Int) {
        _tableConfig.value = TableConfig(color, longScreenSize, mode, pageNumber)
    }

    /**
     * Сохраняет таблицу в указанный Uri с ранее выбранным форматом.
     * Если формат не выбран, операция игнорируется.
     *
     * @param uri Целевой путь сохранения.
     */
    fun saveSchedule(uri: Uri) {
        val format = saveFormat
        if (format != null) {
            saveSchedule(uri, format)
        }
    }

    /**
     * Запоминает формат для последующего сохранения.
     *
     * @param format Формат экспорта: Image или Pdf.
     * @return Unit
     * Исключения: не выбрасывает.
     */
    fun setSaveFormat(format: ExportFormat) {
        saveFormat = format
    }

    /**
     * Запускает сохранение таблицы в указанном формате.
     *
     * @param uri Целевой путь сохранения.
     * @param format Формат экспорта.
     */
    private fun saveSchedule(uri: Uri, format: ExportFormat) {
        val schedule = _schedule.value ?: return
        val config = _tableConfig.value

        exportJob = launchSaveJob(uri, schedule, config, format)
    }

    /**
     * Отправляет таблицу через системный диалог шаринга.
     *
     * @param format Формат экспорта: Image или Pdf.
     * @return Unit
     * Исключения: ошибки экспорта не пробрасываются, публикуются через exportProgress как Error.
     */
    fun sendSchedule(format: ExportFormat) {
        val schedule = _schedule.value ?: return
        val name = schedule.info.scheduleName.ifEmpty { "null" }
        val config = _tableConfig.value

        exportJob = launchSendJob(name, schedule, config, format)
    }

    /**
     * Сбрасывает состояние прогресса экспорта.
     *
     * @return Unit
     * Исключения: не выбрасывает.
     */
    fun exportFinished() {
        _exportProgress.value = ExportProgress.Nothing
    }

    /**
     * Отменяет текущую экспортную задачу и сбрасывает прогресс.
     *
     * @return Unit
     * Исключения: внутренняя отмена может вызвать CancellationException, наружу не пробрасывается.
     */
    fun cancelExport() {
        // Безопасная отмена coroutine Job при наличии активной задачи.
        exportJob?.cancel()
        exportFinished()
    }

    /**
     * Запускает задачу экспорта с последующей отправкой через системный диалог.
     *
     * @param name Имя файла для отправки.
     * @param schedule Модель расписания.
     * @param config Конфигурация таблицы.
     * @param format Формат экспорта.
     * @return Job для контроля выполнения и отмены.
     * Исключения: ошибки экспорта перехватываются и не пробрасываются; отмена может вызвать CancellationException.
     */
    private fun launchSendJob(
        name: String,
        schedule: ScheduleModel,
        config: TableConfig,
        format: ExportFormat
    ): Job = viewModelScope.async {

        _exportProgress.value = ExportProgress.Running

        val exportFlow = when (format) {
            ExportFormat.Image -> tableUseCase.createUriForImage(name, schedule, config)
            ExportFormat.Pdf -> tableUseCase.createUriForPdf(name, schedule, config)
        }

        exportFlow
            .catch { t ->
                // Ошибки экспорта отображаются пользователю и протоколируются в аналитике.
                _exportProgress.value = ExportProgress.Error(t)
                loggerAnalytics.recordException(t)
            }
            .collect { result ->
                _exportProgress.value = ExportProgress.Finished(
                    path = result,
                    type = ExportType.Send,
                    format = format
                )
            }
    }

    /**
     * Запускает задачу экспорта с сохранением по указанному Uri.
     *
     * @param uri Целевой путь сохранения.
     * @param schedule Модель расписания.
     * @param config Конфигурация таблицы.
     * @param format Формат экспорта.
     * @return Job для контроля выполнения и отмены.
     */
    private fun launchSaveJob(
        uri: Uri,
        schedule: ScheduleModel,
        config: TableConfig,
        format: ExportFormat
    ): Job = viewModelScope.async {

        _exportProgress.value = ExportProgress.Running

        val exportFlow = when (format) {
            ExportFormat.Image -> tableUseCase.saveImageTable(schedule, config, uri)
            ExportFormat.Pdf -> tableUseCase.savePdfTable(schedule, config, uri)
        }

        exportFlow
            .catch { t ->
                // Ошибки сохранения отображаются пользователю и протоколируются в аналитике.
                _exportProgress.value = ExportProgress.Error(t)
                loggerAnalytics.recordException(t)
            }
            .collect { result ->
                _exportProgress.value = ExportProgress.Finished(
                    path = result,
                    type = ExportType.Save,
                    format = format
                )
            }
    }
}