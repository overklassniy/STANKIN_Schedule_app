package com.overklassniy.stankinschedule.news.viewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.core.ui.components.UIState
import com.overklassniy.stankinschedule.news.core.domain.model.NewsContent
import com.overklassniy.stankinschedule.news.core.domain.usecase.NewsViewerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
/**
 * ViewModel экрана просмотра новости.
 *
 * Отвечает за загрузку контента новости и трансляцию состояния в UI.
 *
 * Примечания:
 * - Все операции выполняются в scope ViewModel (viewModelScope).
 */
class NewsViewerViewModel @Inject constructor(
    private val viewerUseCase: NewsViewerUseCase,
) : ViewModel() {

    private val _newsContent = MutableStateFlow<UIState<NewsContent>>(UIState.loading())

    /**
     * Состояние загрузки и данных новости для подписки во view.
     *
     * @return Горячий поток состояния [UIState] с моделью [NewsContent].
     */
    val newsContent = _newsContent.asStateFlow()

    /**
     * Загружает контент новости.
     *
     * Алгоритм:
     * 1. Устанавливает состояние загрузки.
     * 2. Запускает use case для загрузки данных.
     * 3. В случае ошибки публикует [UIState.Failed], иначе [UIState.Success].
     *
     * @param postId Идентификатор новости.
     * @param force Принудительно обновить данные, игнорируя кэш.
     */
    fun loadNewsContent(postId: Int, force: Boolean = false) {
        _newsContent.value = UIState.loading()

        viewModelScope.launch {
            viewerUseCase.loadNewsContent(postId, force)
                .catch { e ->
                    _newsContent.value = UIState.failed(e)
                }
                .collect { data ->
                    _newsContent.value = UIState.success(data)
                }
        }
    }
}