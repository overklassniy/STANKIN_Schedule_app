package com.overklassniy.stankinschedule.schedule.repository.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.core.ui.components.UIState
import com.overklassniy.stankinschedule.schedule.repository.domain.model.Course
import com.overklassniy.stankinschedule.schedule.repository.domain.model.Grade
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryCategory
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem
import com.overklassniy.stankinschedule.schedule.repository.domain.usecase.RepositoryUseCase
import com.overklassniy.stankinschedule.schedule.repository.ui.components.DownloadEvent
import com.overklassniy.stankinschedule.schedule.repository.ui.components.DownloadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel экрана репозитория расписаний.
 *
 * Управляет описанием репозитория, списком элементов, фильтрами, поиском и событиями загрузки.
 */
@HiltViewModel
class ScheduleRepositoryViewModel @Inject constructor(
    private val useCase: RepositoryUseCase,
) : ViewModel() {

    private val _description = MutableStateFlow<UIState<RepositoryDescription>>(UIState.loading())
    val description = _description.asStateFlow()

    private var _repositoryItemsCache: List<RepositoryItem>? = null
    private val _repositoryItems =
        MutableStateFlow<UIState<List<RepositoryItem>>>(UIState.loading())
    val repositoryItems = _repositoryItems.asStateFlow()

    private val _download = MutableSharedFlow<DownloadState?>()
    val download = _download.asSharedFlow()

    private val _category = MutableStateFlow<RepositoryCategory?>(null)

    /**
     * Текущая категория репозитория (год/тип), используется для фильтрации.
     */
    @Suppress("unused")
    val category = _category.asStateFlow()

    private val _grade = MutableStateFlow<Grade?>(null)
    val grade = _grade.asStateFlow()

    private val _course = MutableStateFlow<Course?>(null)
    val course = _course.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive = _isSearchActive.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        reloadDescription()
    }

    /**
     * Загружает элементы выбранной категории репозитория.
     *
     * @param category Выбранная категория.
     * @param useCache Использовать кэш при загрузке.
     */
    private fun loadCategory(category: RepositoryCategory, useCache: Boolean = true) {
        _repositoryItems.value = UIState.loading()

        viewModelScope.launch {
            useCase.repositoryItems(category.name, useCache)
                .catch { e ->
                    _repositoryItems.value = UIState.failed(e)
                }
                .collect {
                    _repositoryItemsCache = it
                    updateCategoryFilters()
                }
        }
    }

    /**
     * Фильтр по уровню образования (бакалавриат/магистратура/специалитет/аспирантура).
     *
     * @param item Элемент репозитория.
     * @param currentGrade Выбранный уровень образования.
     *
     * @return [Boolean] Проходит ли элемент фильтр.
     */
    private fun gradeFilter(item: RepositoryItem, currentGrade: Grade?): Boolean {
        if (currentGrade == null) {
            return true
        }

        val part = item.name.split('-').getOrNull(0) ?: return true

        if (part.equals("АСП", ignoreCase = true)) {
            return currentGrade == Grade.Postgraduate
        }

        val itemGrade = when (part.last().uppercaseChar()) {
            'Б' -> Grade.Bachelor
            'М' -> Grade.Magistracy
            'С' -> Grade.Specialist
            else -> null
        } ?: return true

        return itemGrade == currentGrade
    }

    /**
     * Перезагружает текущую категорию репозитория с учетом кэша.
     */
    fun reloadCategory() {
        val currentCategory = _category.value ?: return
        loadCategory(currentCategory)
    }

    /**
     * Обновляет описание репозитория, принудительно отключая кэш.
     */
    fun refresh() {
        reloadDescription(useCache = false)
    }

    /**
     * Загружает описание репозитория и инициализирует первую категорию.
     *
     * @param useCache Использовать ли кэш.
     */
    fun reloadDescription(useCache: Boolean = true) {
        _description.value = UIState.loading()

        viewModelScope.launch {
            useCase.repositoryDescription(useCache)
                .catch { e ->
                    _description.value = UIState.failed(e)
                }
                .collect { description ->
                    val item = description.categories.first()
                    _category.value = item
                    _description.value = UIState.success(description)

                    loadCategory(item, useCache)
                }
        }
    }

    /**
     * Переключает выбранный уровень образования.
     *
     * @param grade Выбираемый уровень.
     */
    fun updateGrade(grade: Grade) {
        if (_grade.value == grade) {
            _grade.value = null
        } else {
            _grade.value = grade
        }
        updateCategoryFilters()
    }

    /**
     * Переключает выбранный курс.
     *
     * @param course Выбираемый курс.
     */
    fun updateCourse(course: Course) {
        if (_course.value == course) {
            _course.value = null
        } else {
            _course.value = course
        }
        updateCategoryFilters()
    }

    /**
     * Обновляет текущую категорию и загружает соответствующие данные.
     *
     * @param category Новая категория.
     */
    @Suppress("unused")
    fun updateCategory(category: RepositoryCategory) {
        if (_category.value != category) {
            _category.value = category
            loadCategory(category)
        }
    }

    /**
     * Включает/выключает режим поиска; при выключении очищает запрос.
     */
    fun toggleSearch() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) {
            updateSearchQuery("")
        }
    }

    /**
     * Обновляет поисковый запрос и пересчитывает фильтры.
     *
     * @param query Текст запроса.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        updateCategoryFilters()
    }

    /**
     * Обрабатывает события загрузки расписания.
     *
     * @param event Событие (начало загрузки и т.д.).
     */
    fun onDownloadEvent(event: DownloadEvent) {
        viewModelScope.launch {
            when (event) {
                is DownloadEvent.StartDownload -> {
                    val isExist = useCase.isScheduleExist(event.scheduleName)
                    if (isExist) {
                        _download.emit(DownloadState.RequiredName(event.scheduleName, event.item))
                    } else {
                        _download.emit(DownloadState.StartDownload(event.scheduleName, event.item))
                    }
                }
            }
        }
    }

    /**
     * Фильтр по курсу, вычисляет ожидаемый год набора группы относительно текущего года категории.
     *
     * @param item: Элемент
     * @param currentCourse Выбранный курс.
     * @param year Год из категории (например, 2024).
     *
     * @return [Boolean] Проходит ли элемент фильтр по курсу.
     */
    private fun courseFilter(item: RepositoryItem, currentCourse: Course?, year: Int?): Boolean {
        if (currentCourse == null || year == null) {
            return true
        }

        val part = item.name.split('-').getOrNull(1) ?: return true
        val groupYear = part.toIntOrNull() ?: return true

        val expectedGroupYear = (year % 100) - (currentCourse.number - 1)

        return expectedGroupYear == groupYear
    }

    /**
     * Применяет все фильтры и поиск к кэшу элементов, сортирует и обновляет состояние.
     *
     * Алгоритм:
     *  1. Фильтрация по уровню образования.
     *  2. Фильтрация по курсу (на основе года).
     *  3. Фильтрация по поисковому запросу (нормализация без пробелов/дефисов, lowercase).
     *  4. Сортировка по типу образования, году набора и названию.
     */
    private fun updateCategoryFilters() {
        val cache = _repositoryItemsCache ?: return

        val currentGrade = _grade.value
        val currentCourse = _course.value
        val currentYear = _category.value?.year
        val currentQuery = _searchQuery.value

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.Default) {
            val filterItems = cache
                .asSequence()
                .filter { item ->
                    gradeFilter(item, currentGrade)
                }
                .filter { item ->
                    courseFilter(item, currentCourse, currentYear)
                }
                .filter { item ->
                    if (currentQuery.isBlank()) return@filter true
                    fun normalize(s: String): String =
                        s.replace(" ", "").replace("-", "").lowercase()

                    val normalizedName = normalize(item.name)
                    val normalizedQuery = normalize(currentQuery)

                    normalizedName.contains(normalizedQuery)
                }
                .sortedWith(
                    compareBy<RepositoryItem> { item ->
                        val part = item.name.split('-').getOrNull(0) ?: ""
                        when {
                            part.endsWith('Б', ignoreCase = true) -> 1
                            part.endsWith('С', ignoreCase = true) -> 2
                            part.endsWith('М', ignoreCase = true) -> 3
                            part.equals("АСП", ignoreCase = true) -> 4
                            else -> 5
                        }
                    }.thenBy { item ->
                        val part = item.name.split('-').getOrNull(1) ?: "0"
                        -(part.toIntOrNull() ?: 0)
                    }.thenBy { item ->
                        item.name
                    }
                )
                .toList()

            _repositoryItems.value = UIState.success(filterItems)
        }
    }
}

