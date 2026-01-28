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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsPostColumn(
    posts: Flow<PagingData<NewsPost>>,
    onClick: (post: NewsPost) -> Unit,
    isNewsRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = newsImageLoader(LocalContext.current),
    columnState: LazyListState = rememberLazyListState()
) {
    val lazyPostItems: LazyPagingItems<NewsPost> = posts.collectAsLazyPagingItems()

    // Removed pull-to-refresh to fix horizontal swipe gesture conflict with HorizontalPager
    // Refresh is triggered automatically when switching tabs or on initial load
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