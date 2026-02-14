package com.overklassniy.stankinschedule.schedule.viewer.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.usecase.ScheduleDeviceUseCase
import com.overklassniy.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.overklassniy.stankinschedule.schedule.ical.domain.usecase.ICalExporterUseCase
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.usecase.ScheduleSettingsUseCase
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.overklassniy.stankinschedule.schedule.viewer.domain.usecase.ScheduleViewerUseCase
import com.overklassniy.stankinschedule.schedule.viewer.ui.components.CalendarDayData
import com.overklassniy.stankinschedule.schedule.viewer.ui.components.ExportFormat
import com.overklassniy.stankinschedule.schedule.viewer.ui.components.ExportProgress
import com.overklassniy.stankinschedule.schedule.viewer.ui.components.RenameEvent
import com.overklassniy.stankinschedule.schedule.viewer.ui.components.RenameState
import com.overklassniy.stankinschedule.schedule.viewer.ui.components.ScheduleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import java.net.URL
import javax.inject.Inject

/**
 * ViewModel для экрана просмотра расписания.
 *
 * Назначение: загружает модель расписания, управляет листингом дней,
 * сохранением в файлы и операциями переименования и удаления.
 * Содержит состояния: scheduleState, saveProgress, renameState, а также настройки отображения.
 */
@HiltViewModel
class ScheduleViewerViewModel @Inject constructor(
    private val viewerUseCase: ScheduleViewerUseCase,
    private val scheduleUseCase: ScheduleUseCase,
    private val scheduleDeviceUseCase: ScheduleDeviceUseCase,
    settingsUseCase: ScheduleSettingsUseCase,
    private val iCalExporterUseCase: ICalExporterUseCase,
    private val handle: SavedStateHandle
) : ViewModel() {

    /**
     * Признак вертикального просмотра.
     * Воздействует на выбор контейнера списка (LazyColumn или LazyRow).
     */
    val isVerticalViewer: Flow<Boolean> = settingsUseCase.isVerticalViewer()

    /**
     * Группа цветов пар.
     * Воздействует на палитру карточек пар.
     */
    val pairColorGroup: Flow<PairColorGroup> = settingsUseCase.pairColorGroup()

    private val _scheduleState = MutableStateFlow<ScheduleState>(ScheduleState.Loading)

    /** Публичное состояние экрана расписания. */
    val scheduleState = _scheduleState.asStateFlow()

    private val clearPager = Channel<Unit>(Channel.CONFLATED)

    /**
     * Текущая дата для отображения в pager.
     * Если в SavedStateHandle есть сохраненное значение, используется оно.
     * В противном случае возвращается текущая дата.
     */
    val currentDay: LocalDate
        get() {
            val dateString = handle.get<String>(CURRENT_PAGER_DATE)
            return if (dateString != null) {
                try {
                    LocalDate.parse(dateString)
                } catch (_: Exception) {
                    LocalDate.now()
                }
            } else {
                LocalDate.now()
            }
        }
    private val _scheduleStartDay = MutableStateFlow(currentDay)

    private var _scheduleId: Long = -1
    private val _schedule = MutableStateFlow<ScheduleModel?>(null)

    private var _saveFormat = ExportFormat.Json
    private val _saveProgress = MutableStateFlow<ExportProgress>(ExportProgress.Nothing)

    /** Публичное состояние прогресса сохранения файла. */
    val saveProgress = _saveProgress.asStateFlow()

    private val _renameState = MutableStateFlow<RenameState?>(null)

    /** Публичное состояние диалога переименования. */
    val renameState = _renameState.asStateFlow()

    /**
     * Поток страниц дней расписания для отображения.
     *
     * Алгоритм:
     * 1. При очистке pager отправляет пустые данные для плавного обновления.
     * 2. Комбинирует модель и начальную дату, создает Pager при наличии модели.
     * 3. Выносит работу с источником на IO и кэширует в scope ViewModel.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val scheduleDays: Flow<PagingData<ScheduleViewDay>> =
        flowOf(
            clearPager.receiveAsFlow().map { PagingData.empty() },
            combine(_schedule, _scheduleStartDay) { model, _ -> model }
                .flatMapLatest { model ->
                    if (model != null) createPager(model, currentDay).flow else emptyFlow()
                }
                .flowOn(Dispatchers.IO).cachedIn(viewModelScope)
        ).flattenMerge(2)

    /**
     * Создает Pager для постраничного отображения дней расписания.
     *
     * @param model Модель расписания.
     * @param currentDay Текущая дата для начального ключа.
     * @return Pager, конфигурированный на 60 элементов в странице.
     */
    private fun createPager(
        model: ScheduleModel,
        currentDay: LocalDate
    ): Pager<LocalDate, ScheduleViewDay> {
        return Pager(
            config = PagingConfig(
                pageSize = 60, // размер страницы 60 дней для комфортной прокрутки
                initialLoadSize = 60, // начальная загрузка соответствует размеру страницы
                prefetchDistance = 30, // предзагрузка на 30 элементов для плавности
                enablePlaceholders = false
            ),
            initialKey = currentDay,
            pagingSourceFactory = { viewerUseCase.scheduleSource(model) }
        )
    }

    /**
     * Загружает расписание и подготавливает состояние экрана.
     *
     * @param scheduleId Идентификатор расписания.
     * @param startDate Начальная дата для отображения, может быть null.
     */
    fun loadSchedule(scheduleId: Long, startDate: LocalDate?) {
        // Если расписание уже загружено, но есть новая дата - применяем её
        if (_scheduleId == scheduleId) {
            if (startDate != null) {
                selectDate(startDate)
            }
            return
        }

        if (startDate != null) updatePagingDate(startDate)

        viewModelScope.launch {
            scheduleUseCase.scheduleModel(scheduleId)
                .collect { model ->
                    // нет такого расписания
                    if (model == null) {
                        _scheduleState.value = ScheduleState.NotFound
                    } else {
                        _scheduleState.value = ScheduleState.Success(
                            scheduleName = model.info.scheduleName,
                            isEmpty = model.isEmpty()
                        )

                        if (startDate == null) {
                            val limitedDate = model.limitDate(currentDay)
                            if (limitedDate != currentDay) {
                                updatePagingDate(limitedDate)
                                _scheduleStartDay.value = limitedDate
                            }
                        }

                        clearPager.send(Unit)
                        _schedule.value = model
                        _scheduleId = model.info.id
                    }
                }
        }
    }

    /**
     * Устанавливает формат сохранения.
     *
     * @param format Формат: Json или ICal.
     */
    fun setSaveFormat(format: ExportFormat) {
        _saveFormat = format
    }

    /**
     * Сбрасывает состояние прогресса сохранения.
     */
    fun saveFinished() {
        _saveProgress.value = ExportProgress.Nothing
    }

    /**
     * Выбирает новую дату для отображения.
     * Если дата совпадает с текущей, ничего не делает.
     *
     * @param date Новая дата.
     */
    fun selectDate(date: LocalDate) {
        if (date == currentDay) return

        viewModelScope.launch {
            updatePagingDate(date)

            clearPager.send(Unit)
            _scheduleStartDay.value = date
        }
    }

    /**
     * Сохраняет текущую дату для восстановления после обновления данных.
     *
     * @param currentPagingDate Текущая дата или null.
     */
    fun updatePagingDate(currentPagingDate: LocalDate?) {
        handle[CURRENT_PAGER_DATE] = currentPagingDate?.toString()
    }

    /**
     * Обрабатывает событие диалога переименования.
     *
     * @param event Событие: Rename или Cancel.
     */
    fun onRenameEvent(event: RenameEvent) {
        _renameState.value = when (event) {
            is RenameEvent.Rename -> RenameState.Rename
            is RenameEvent.Cancel -> null
        }
    }

    /**
     * Удаляет текущее расписание и переводит состояние в NotFound.
     */
    fun removeSchedule() {
        viewModelScope.launch {
            scheduleUseCase.removeSchedule(_scheduleId)
            _scheduleState.value = ScheduleState.NotFound
        }
    }

    /**
     * Сохраняет расписание в выбранном формате по указанному Uri.
     *
     * @param uri Целевой путь сохранения.
     */
    fun saveAs(uri: Uri) {
        viewModelScope.launch {
            when (_saveFormat) {
                ExportFormat.Json -> {
                    saveAsJson(uri)
                }

                ExportFormat.ICal -> {
                    saveAsICal(uri)
                }
            }
        }
    }

    /**
     * Сохраняет расписание в формате JSON.
     *
     * @param uri Целевой путь сохранения.
     */
    private suspend fun saveAsJson(uri: Uri) {
        scheduleDeviceUseCase.saveToDevice(_scheduleId, uri.toString())
            .catch { e ->
                // Ошибки сохранения отображаются пользователю.
                _saveProgress.value = ExportProgress.Error(e)
            }
            .collect {
                _saveProgress.value = ExportProgress.Finished(uri, _saveFormat)
            }
    }

    /**
     * Сохраняет расписание в формате iCalendar (.ics).
     *
     * @param uri Целевой путь сохранения.
     */
    private suspend fun saveAsICal(uri: Uri) {
        val currentSchedule = _schedule.value
        if (currentSchedule != null) {
            iCalExporterUseCase.exportSchedule(currentSchedule, uri.toString())
                .catch { e ->
                    _saveProgress.value = ExportProgress.Error(e)
                }
                .collect {
                    _saveProgress.value = ExportProgress.Finished(uri, _saveFormat)
                }
        }
    }

    /**
     * Выполняет переименование расписания.
     *
     * @param newName Новое имя расписания.
     */
    fun renameSchedule(newName: String) {
        viewModelScope.launch {
            scheduleUseCase.renameSchedule(_scheduleId, newName)
                .catch { e ->
                    _renameState.value = RenameState.Error(e)
                    e.printStackTrace()
                }
                .collect { isRename ->
                    _renameState.value = if (isRename) {
                        RenameState.Success
                    } else {
                        RenameState.AlreadyExist()
                    }
                }
        }
    }

    private val _calendarDayData = MutableStateFlow<Map<LocalDate, CalendarDayData>>(emptyMap())

    /** Данные для отображения в календаре (точки пар, выходные). */
    val calendarDayData = _calendarDayData.asStateFlow()

    /** Кэш данных isdayoff: "year-month" -> Set<Int> (номера выходных дней). */
    private val dayOffCache = mutableMapOf<String, Set<Int>>()

    /**
     * Загружает данные календаря для указанного месяца.
     *
     * Вычисляет пары по датам из модели расписания и загружает информацию
     * о выходных днях из isdayoff.ru.
     *
     * @param year Год.
     * @param month Месяц (1-12).
     */
    fun loadCalendarMonth(year: Int, month: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedule = _schedule.value
            val firstDay = LocalDate(year, month, 1)
            val daysInMonth = firstDay.dayOfMonth().maximumValue

            // Загружаем данные о выходных
            val dayOffSet = loadDayOff(year, month)

            val dataMap = mutableMapOf<LocalDate, CalendarDayData>()
            for (day in 1..daysInMonth) {
                val date = LocalDate(year, month, day)
                val pairs = schedule?.pairsByDate(date) ?: emptyList()
                val pairTypes = pairs.map { it.type }
                val isDayOff = dayOffSet.contains(day)

                dataMap[date] = CalendarDayData(
                    pairTypes = pairTypes,
                    isDayOff = isDayOff
                )
            }

            _calendarDayData.value = dataMap
        }
    }

    /**
     * Загружает данные о выходных днях из isdayoff.ru.
     *
     * @param year Год.
     * @param month Месяц (1-12).
     * @return Множество номеров выходных дней в месяце.
     */
    private fun loadDayOff(year: Int, month: Int): Set<Int> {
        val cacheKey = "$year-$month"
        dayOffCache[cacheKey]?.let { return it }

        return try {
            val monthStr = month.toString().padStart(2, '0')
            val url = "https://isdayoff.ru/api/getdata?cc=ru&sd=1&year=$year&month=$monthStr"
            val response = URL(url).readText()

            val dayOffDays = mutableSetOf<Int>()
            response.forEachIndexed { index, char ->
                if (char == '1') {
                    dayOffDays.add(index + 1) // Дни нумеруются с 1
                }
            }

            dayOffCache[cacheKey] = dayOffDays
            dayOffDays
        } catch (e: Exception) {
            Log.e("ScheduleViewerVM", "Failed to load day off data", e)
            emptySet()
        }
    }

    companion object {
        /** Ключ для сохранения текущей даты pager в SavedStateHandle. */
        private const val CURRENT_PAGER_DATE = "current_pager_date"
    }
}