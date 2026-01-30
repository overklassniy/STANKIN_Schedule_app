package com.overklassniy.stankinschedule.schedule.parser.ui

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.core.domain.repository.LoggerAnalytics
import com.overklassniy.stankinschedule.core.domain.usecase.DeviceUseCase
import com.overklassniy.stankinschedule.core.ui.components.UIState
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParseResult
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParserSettings
import com.overklassniy.stankinschedule.schedule.parser.domain.usecase.ParserUseCase
import com.overklassniy.stankinschedule.schedule.parser.ui.model.ParsedFile
import com.overklassniy.stankinschedule.schedule.parser.ui.model.ParserState
import com.overklassniy.stankinschedule.schedule.parser.ui.model.SaveScheduleError
import com.overklassniy.stankinschedule.schedule.parser.ui.model.SelectedFile
import com.overklassniy.stankinschedule.schedule.table.domain.model.ScheduleTable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel процесса импорта расписания из PDF.
 *
 * Управляет шагами мастера, выбранным файлом, настройками парсера и результатами.
 */
@HiltViewModel
class ScheduleParserViewModel @Inject constructor(
    private val deviceUseCase: DeviceUseCase,
    private val parserUseCase: ParserUseCase,
    private val loggerAnalytics: LoggerAnalytics,
    private val scheduleUseCase: ScheduleUseCase
) : ViewModel() {

    private val _parserState = MutableStateFlow<ParserState>(ParserState.SelectFile())

    /**
     * Публичное состояние пошагового процесса импорта.
     */
    val parserState = _parserState.asStateFlow()

    private var _selectedFile: SelectedFile? = null
    private var _parserSettings: ParserSettings = ParserSettings(
        scheduleYear = LocalDate.now().year,
        parserThreshold = 1f
    )
    private var _parserResult: ParsedFile? = null
    private var _scheduleName: String = ""

    /**
     * Выбирает файл по Uri, извлекает имя и рендерит превью.
     *
     * @param uri Ссылка на PDF.
     */
    fun selectFile(uri: Uri) {
        viewModelScope.launch {
            try {
                val filename = deviceUseCase.extractFilename(uri.toString())
                    .substringBeforeLast('.')
                val preview = parserUseCase.renderPreview(uri.toString())
                val selectedFile = SelectedFile(uri, filename)

                _parserState.value = ParserState.SelectFile(selectedFile, preview)
                _selectedFile = selectedFile

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Выбирает файл по пути и имени, рендерит превью.
     *
     * @param filePath Путь к PDF.
     * @param fileName Имя расписания.
     */
    fun selectFileFromPath(filePath: String, fileName: String) {
        viewModelScope.launch {
            try {
                val preview = parserUseCase.renderPreview(filePath)
                val uri = filePath.toUri()
                val selectedFile = SelectedFile(uri, fileName)

                _parserState.value = ParserState.SelectFile(selectedFile, preview)
                _selectedFile = selectedFile
                _scheduleName = fileName

            } catch (e: Exception) {
                e.printStackTrace()
                loggerAnalytics.recordException(e)
            }
        }
    }

    /**
     * Обновляет настройки парсера.
     *
     * @param settings Новые настройки парсера.
     */
    fun onSetupSettings(settings: ParserSettings) {
        _parserSettings = settings
    }

    /**
     * Обновляет имя расписания перед сохранением.
     *
     * @param scheduleName Новое имя расписания.
     */
    fun onScheduleNameChanged(scheduleName: String) {
        _scheduleName = scheduleName
    }

    /**
     * Проверяет корректность имени расписания и уникальность, затем инициирует сохранение.
     *
     * @param scheduleName Имя расписания для проверки.
     * @param currentResult Текущий результат парсинга.
     */
    private fun checkScheduleName(scheduleName: String, currentResult: ParsedFile) {
        viewModelScope.launch {
            if (scheduleName.isEmpty()) {
                _parserState.value = ParserState.SaveResult(
                    scheduleName = scheduleName,
                    saveScheduleError = SaveScheduleError.InvalidScheduleName
                )
                return@launch
            }

            val isExists = scheduleUseCase.isScheduleExists(scheduleName)
            if (isExists) {
                _parserState.value = ParserState.SaveResult(
                    scheduleName = scheduleName,
                    saveScheduleError = SaveScheduleError.ScheduleNameAlreadyExists
                )
                return@launch
            }

            val schedule = createScheduleModel(scheduleName, currentResult.successResult)
            saveSchedule(schedule)
        }
    }

    /**
     * Сохраняет модель расписания и обновляет состояние завершения импорта.
     *
     * @param schedule Модель расписания для сохранения.
     */
    private fun saveSchedule(schedule: ScheduleModel) {
        viewModelScope.launch {
            _parserState.value = ParserState.ImportFinish(state = UIState.loading())

            scheduleUseCase.createSchedule(schedule)
                .catch { e ->
                    _parserState.value = ParserState.ImportFinish(state = UIState.failed(e))
                }
                .collectLatest { isCreated ->
                    if (isCreated) {
                        _parserState.value = ParserState.ImportFinish(
                            state = UIState.success(Unit)
                        )
                    } else {
                        val error = IllegalArgumentException("Failed to create a schedule")
                        _parserState.value = ParserState.ImportFinish(
                            state = UIState.failed(error)
                        )
                    }
                }
        }
    }

    /**
     * Запускает парсинг PDF по выбранному файлу и настройкам.
     * Раскладывает результаты на success/missing/error и формирует ParsedFile.
     *
     * @param selectedFile Выбранный файл.
     * @param settings Настройки парсера.
     */
    private fun startScheduleParser(
        selectedFile: SelectedFile,
        settings: ParserSettings
    ) {
        viewModelScope.launch {
            try {
                _parserState.value = ParserState.ParserResult(UIState.loading())

                val result = parserUseCase.parsePDF(selectedFile.path.toString(), settings)

                val successResult = mutableListOf<ParseResult.Success>()
                val missingResult = mutableListOf<ParseResult.Missing>()
                val errorResult = mutableListOf<ParseResult.Error>()
                // Раскладываем результаты по типам: success/missing/error
                for (r in result) {
                    when (r) {
                        is ParseResult.Success -> successResult += r
                        is ParseResult.Error -> errorResult += r
                        is ParseResult.Missing -> missingResult += r
                    }
                }

                val scheduleName = selectedFile.filename
                val schedule = createScheduleModel(scheduleName, successResult)

                val parsedFile = ParsedFile(
                    successResult = successResult,
                    missingResult = missingResult,
                    errorResult = errorResult,
                    table = ScheduleTable(schedule)
                )

                _parserResult = parsedFile
                _parserState.value = ParserState.ParserResult(UIState.success(parsedFile))

            } catch (e: Exception) {
                _parserState.value = ParserState.ParserResult(UIState.failed(e))
                loggerAnalytics.recordException(e)
            }
        }
    }

    /**
     * Создаёт модель расписания из успешных результатов парсинга.
     *
     * @param scheduleName Имя расписания.
     * @param successResult Список успешно распознанных пар.
     * @return Модель расписания.
     */
    private fun createScheduleModel(
        scheduleName: String,
        successResult: List<ParseResult.Success>
    ): ScheduleModel {
        val schedule = ScheduleModel(info = ScheduleInfo(scheduleName))
        successResult.forEach {
            try {
                schedule.add(it.pair)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return schedule
    }

    /**
     * Переход назад по шагам мастера.
     */
    fun back() {
        when (_parserState.value) {
            is ParserState.Settings -> {
                val selectedFile = _selectedFile
                if (selectedFile != null) {
                    viewModelScope.launch {
                        try {
                            val preview = parserUseCase.renderPreview(selectedFile.path.toString())
                            _parserState.value = ParserState.SelectFile(selectedFile, preview)
                        } catch (_: Exception) {
                            // Если не удалось загрузить превью, возвращаемся без него
                            _parserState.value = ParserState.SelectFile(selectedFile, null)
                        }
                    }
                } else {
                    _parserState.value = ParserState.SelectFile()
                }
            }

            is ParserState.ParserResult -> {
                _parserState.value = ParserState.Settings(_parserSettings)
            }

            is ParserState.SaveResult -> {
                val currentResult = _parserResult
                if (currentResult != null) {
                    _parserState.value = ParserState.ParserResult(UIState.success(currentResult))
                } else {
                    _parserState.value = ParserState.SelectFile()
                }
            }

            else -> {
                // SelectFile и ImportFinish - ничего не делаем, обрабатывается Activity
            }
        }
    }

    /**
     * Переход на следующий шаг мастера.
     */
    fun next() {
        when (_parserState.value) {
            is ParserState.SelectFile -> {
                if (_selectedFile != null) {
                    _parserState.value = ParserState.Settings(_parserSettings)
                }
            }

            is ParserState.Settings -> {
                val currentSelectedFile = _selectedFile
                if (currentSelectedFile != null) {
                    startScheduleParser(currentSelectedFile, _parserSettings)
                }
            }

            is ParserState.ParserResult -> {
                val name = _scheduleName.ifEmpty { _selectedFile?.filename ?: "" }
                _scheduleName = name
                _parserState.value = ParserState.SaveResult(name)
            }

            is ParserState.SaveResult -> {
                val currentResult = _parserResult
                if (currentResult != null) {
                    checkScheduleName(_scheduleName, currentResult)
                } else {
                    _parserState.value = ParserState.SelectFile()
                }
            }
        }
    }
}