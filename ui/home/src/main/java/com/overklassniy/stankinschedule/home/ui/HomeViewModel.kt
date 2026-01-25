package com.overklassniy.stankinschedule.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.core.domain.settings.ApplicationPreference
import com.overklassniy.stankinschedule.core.ui.components.UIState
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.core.domain.usecase.NewsReviewUseCase
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleInfo
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.core.domain.usecase.ScheduleUseCase
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.settings.domain.usecase.ScheduleSettingsUseCase
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.overklassniy.stankinschedule.schedule.viewer.domain.usecase.ScheduleViewerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scheduleUseCase: ScheduleUseCase,
    private val scheduleViewerUseCase: ScheduleViewerUseCase,
    private val scheduleSettingsUseCase: ScheduleSettingsUseCase,
    private val newsUseCase: NewsReviewUseCase,
    private val applicationPreference: ApplicationPreference
) : ViewModel() {

    val pairColorGroup: Flow<PairColorGroup> = scheduleSettingsUseCase.pairColorGroup()

    private val _favorite = MutableStateFlow<ScheduleInfo?>(null)
    val favorite = _favorite.asStateFlow()

    private val _days = MutableStateFlow<UIState<List<ScheduleViewDay>>>(UIState.loading())
    val days = _days.asStateFlow()

    private val _news = MutableStateFlow<List<NewsPost>>(emptyList())
    val news = _news.asStateFlow()

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

        viewModelScope.launch {
            newsUseCase.lastNews(newsCount = NEWS_COUNT).collectLatest {
                _news.value = it
            }
        }

        viewModelScope.launch {
            try {
                newsUseCase.refreshAllNews(force = false)
            } catch (ignored: Exception) {

            }
        }
    }

    fun saveLastUpdate(last: DateTime) {
        applicationPreference.lastInAppUpdate = last
    }

    fun currentLastUpdate(): DateTime? {
        return applicationPreference.lastInAppUpdate
    }

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

    companion object {
        const val NEWS_COUNT = 12
    }
}