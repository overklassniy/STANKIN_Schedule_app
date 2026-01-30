package com.overklassniy.stankinschedule.news.review.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.ImageLoader
import com.overklassniy.stankinschedule.core.ui.components.PagingLazyColumn
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.core.ui.utils.newsImageLoader
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import kotlinx.coroutines.flow.Flow

/**
 * Колонка с постами новостей на основе Paging.
 *
 * Показывает список карточек новостей с обработкой состояний загрузки
 * и ошибок (initial/append). Клик по карточке передаёт модель вовне.
 *
 * @param posts Поток постраничных данных новостей.
 * @param onClick Обработчик клика по карточке новости.
 * @param modifier Модификатор для внешнего оформления.
 * @param imageLoader Загрузчик изображений.
 * @param columnState Состояние списка для прокрутки.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
@Suppress("Unused")
fun NewsPostColumn(
    posts: Flow<PagingData<NewsPost>>,
    onClick: (post: NewsPost) -> Unit,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = newsImageLoader(LocalContext.current),
    columnState: LazyListState = rememberLazyListState()
) {
    val lazyPostItems: LazyPagingItems<NewsPost> = posts.collectAsLazyPagingItems()

    // Обновление списка выполняется автоматически при начальной загрузке данных и при переключении вкладок.
    // Явный Pull-to-Refresh здесь не используется, чтобы избежать конфликтов с горизонтальными жестами.
    PagingLazyColumn(
        state = columnState,
        pagingItems = lazyPostItems,
        modifier = modifier.fillMaxSize(),
        key = lazyPostItems.itemKey { it.id },
        onContent = { index ->
            val post = lazyPostItems[index]
            NewsPost(
                post = post,
                imageLoader = imageLoader,
                onClick = onClick,
                modifier = Modifier
                    .padding(8.dp)
                    .animateItem()
            )
            HorizontalDivider()
        },
        onContentLoading = {
            NewsLoading(modifier = Modifier.fillParentMaxWidth())
        },
        onContentError = { throwable ->
            NewsError(
                error = throwable,
                onRetry = { lazyPostItems.retry() },
                modifier = Modifier.fillParentMaxWidth()
            )
        },
        onAppendLoading = {
            NewsLoading(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(Dimen.ContentPadding)
            )
        },
        onAppendError = { throwable ->
            NewsError(
                error = throwable,
                onRetry = { lazyPostItems.retry() },
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(Dimen.ContentPadding)
            )
        }
    )
}