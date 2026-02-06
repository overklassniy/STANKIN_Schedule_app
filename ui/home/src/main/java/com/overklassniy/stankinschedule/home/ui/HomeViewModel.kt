package com.overklassniy.stankinschedule.home.ui

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.core.domain.ext.subHours
import com.overklassniy.stankinschedule.core.domain.repository.UpdateRepository
import com.overklassniy.stankinschedule.core.domain.settings.ApplicationPreference
import com.overklassniy.stankinschedule.core.ui.components.UIState
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision
import com.overklassniy.stankinschedule.news.core.domain.usecase.NewsReviewUseCase
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.usecase.ScheduleSettingsUseCase
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.overklassniy.stankinschedule.schedule.viewer.domain.usecase.ScheduleViewerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel для главного экрана приложения.
 * Управляет отображением избранного расписания, новостей и навигацией.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase,
    private val scheduleViewerUseCase: ScheduleViewerUseCase,
    private val scheduleSettingsUseCase: ScheduleSettingsUseCase,
    private val newsUseCase: NewsReviewUseCase,
    private val applicationPreference: ApplicationPreference,
    private val updateRepository: UpdateRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    val pairColorGroup: Flow<PairColorGroup> = scheduleSettingsUseCase.pairColorGroup()

    private val _hasUpdate = MutableStateFlow(applicationPreference.hasUpdate())
    val hasUpdate = _hasUpdate.asStateFlow()

    private val _favorite = MutableStateFlow<ScheduleInfo?>(null)
    val favorite = _favorite.asStateFlow()

    private val _days = MutableStateFlow<UIState<List<ScheduleViewDay>>>(UIState.loading())
    val days = _days.asStateFlow()

    private val _universityNews = MutableStateFlow<List<NewsPost>>(emptyList())
    val universityNews = _universityNews.asStateFlow()

    private val _announcementsNews = MutableStateFlow<List<NewsPost>>(emptyList())
    val announcementsNews = _announcementsNews.asStateFlow()

    private val _deanNews = MutableStateFlow<List<NewsPost>>(emptyList())
    val deanNews = _deanNews.asStateFlow()

    private val _exchangeNews = MutableStateFlow<List<NewsPost>>(emptyList())
    val exchangeNews = _exchangeNews.asStateFlow()

    private val _PhDNews = MutableStateFlow<List<NewsPost>>(emptyList())
    val PhDNews = _PhDNews.asStateFlow()

    private val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    private fun appVersion(): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "0.0.0"
        } catch (_: Exception) {
            "0.0.0"
        }
    }

    private data class DaysKey(
        val scheduleId: Long,
        val from: LocalDate,
        val to: LocalDate,
    )

    private var lastDaysKey: DaysKey? = null
    private var lastDaysValue: List<ScheduleViewDay> = emptyList()

    init {
        val delta = 3
        viewModelScope.launch {
            scheduleSettingsUseCase.favorite().collectLatest { id ->
                scheduleUseCase.scheduleModel(id).collectLatest { model ->
                    updateScheduleBlock(model, delta)
                }
            }
        }

        // Загрузка новостей университета
        viewModelScope.launch {
            newsUseCase.lastNews(
                newsSubdivision = NewsSubdivision.University.id,
                newsCount = NEWS_COUNT
            ).collectLatest {
                _universityNews.value = it
            }
        }

        // Загрузка анонсов
        viewModelScope.launch {
            newsUseCase.lastNews(
                newsSubdivision = NewsSubdivision.Announcements.id,
                newsCount = NEWS_COUNT
            ).collectLatest {
                _announcementsNews.value = it
            }
        }

        // Загрузка новостей деканата
        viewModelScope.launch {
            newsUseCase.lastNews(
                newsSubdivision = NewsSubdivision.Dean.id,
                newsCount = NEWS_COUNT
            ).collectLatest {
                _deanNews.value = it
            }
        }

        // Загрузка международных новостей
        viewModelScope.launch {
            newsUseCase.lastNews(
                newsSubdivision = NewsSubdivision.Exchange.id,
                newsCount = NEWS_COUNT
            ).collectLatest {
                _exchangeNews.value = it
            }
        }

        // Загрузка новостей аспирантуры
        viewModelScope.launch {
            newsUseCase.lastNews(
                newsSubdivision = NewsSubdivision.PhD.id,
                newsCount = NEWS_COUNT
            ).collectLatest {
                _PhDNews.value = it
            }
        }

        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            try {
                newsUseCase.refreshNews(NewsSubdivision.University.id, force = false)
                newsUseCase.refreshNews(NewsSubdivision.Announcements.id, force = false)
                newsUseCase.refreshNews(NewsSubdivision.Dean.id, force = false)
                newsUseCase.refreshNews(NewsSubdivision.Exchange.id, force = false)
                newsUseCase.refreshNews(NewsSubdivision.PhD.id, force = false)
            } catch (_: Exception) {
                // Silent fail
            }
        }

        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            checkForUpdates()
        }
    }

    /**
     * Проверяет наличие обновлений через GitHub Releases и обновляет кеш.
     *
     * Выполняется не чаще, чем раз в 24 часа. В режиме отладки пропускается.
     */
    private suspend fun checkForUpdates() {
        val lastCheck = applicationPreference.lastUpdateCheck
        val shouldCheck = lastCheck == null || (lastCheck subHours DateTime.now()) > 24

        if (shouldCheck) {
            try {
                if (isDebug) return
                val currentVersion = appVersion()
                val update = updateRepository.checkForUpdate(currentVersion)

                applicationPreference.lastUpdateCheck = DateTime.now()

                if (update != null) {
                    applicationPreference.availableUpdateVersion = update.latestVersion
                    applicationPreference.availableUpdateChangelog = update.changelog
                    applicationPreference.availableUpdateUrl = update.downloadUrl
                    _hasUpdate.value = true
                } else {
                    applicationPreference.clearUpdate()
                    _hasUpdate.value = false
                }
            } catch (_: Exception) {
                // Don't update state on error, keep cached value
            }
        }
    }

    /**
     * Сохраняет момент завершения In‑App Update.
     *
     * @param last Время последнего обновления.
     */
    fun saveLastUpdate(last: DateTime) {
        applicationPreference.lastInAppUpdate = last
    }

    /**
     * Возвращает момент последнего In‑App Update.
     *
     * @return Дата/время последнего обновления или null.
     */
    fun currentLastUpdate(): DateTime? {
        return applicationPreference.lastInAppUpdate
    }

    /**
     * Обновляет блок расписания вокруг текущей даты и кэширует результат.
     *
     * @param model Модель расписания.
     * @param delta Число дней до/после текущей даты.
     */
    private suspend fun updateScheduleBlock(model: ScheduleModel?, delta: Int) {
        if (model == null) {
            _favorite.value = null
            _days.value = UIState.Success(emptyList())
            return
        }

        val now = LocalDate.now()
        val from = now.minusDays(delta)
        val to = now.plusDays(delta + 1)
        val key = DaysKey(
            scheduleId = model.info.id,
            from = from,
            to = to,
        )

        val days = if (key == lastDaysKey) {
            lastDaysValue
        } else {
            val computed = withContext(Dispatchers.Default) {
                scheduleViewerUseCase.scheduleViewDays(
                    model = model,
                    from = from,
                    to = to
                )
            }
            lastDaysKey = key
            lastDaysValue = computed
            computed
        }

        _favorite.value = model.info
        _days.value = UIState.success(days)
    }

    /**
     * Константы для главного экрана.
     * NEWS_COUNT — количество карточек новостей.
     */
    companion object {
        const val NEWS_COUNT = 12
    }
}