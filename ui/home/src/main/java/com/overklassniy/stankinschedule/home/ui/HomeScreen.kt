package com.overklassniy.stankinschedule.home.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import com.overklassniy.stankinschedule.core.ui.components.Stateful
import com.overklassniy.stankinschedule.core.ui.components.TrackCurrentScreen
import com.overklassniy.stankinschedule.core.ui.ext.Zero
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.core.ui.utils.BrowserUtils
import com.overklassniy.stankinschedule.core.ui.utils.newsImageLoader
import com.overklassniy.stankinschedule.home.ui.components.InAppUpdateDialog
import com.overklassniy.stankinschedule.home.ui.components.rememberInAppUpdater
import com.overklassniy.stankinschedule.home.ui.components.schedule.ScheduleHome
import com.overklassniy.stankinschedule.home.ui.data.UpdateState
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.review.ui.components.NewsPost
import com.overklassniy.stankinschedule.schedule.core.ui.toColor
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToSchedule: (scheduleId: Long) -> Unit,
    navigateToNews: () -> Unit,
    navigateToNewsPost: (post: NewsPost) -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = newsImageLoader(LocalContext.current)
) {
    TrackCurrentScreen(screen = "HomeScreen")

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_home),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                actions = {
                    IconButton(
                        onClick = navigateToSettings
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.Zero,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->

        val context = LocalContext.current
        val columnState = rememberLazyListState()

        val updateState = rememberInAppUpdater(
            saveLastUpdate = viewModel::saveLastUpdate,
            currentLastUpdate = viewModel::currentLastUpdate
        )
        LaunchedEffect(updateState.progress.value) {
            if (updateState.progress.value is UpdateState.UpdateRequired) {
                columnState.animateScrollToItem(0)
                scrollBehavior.state.contentOffset = 0f
            }
        }

        val favorite by viewModel.favorite.collectAsStateWithLifecycle()
        val scheduleDays by viewModel.days.collectAsStateWithLifecycle()
        val pairColorGroup by viewModel.pairColorGroup.collectAsStateWithLifecycle(PairColorGroup.default())
        val pairColors by remember(pairColorGroup) { derivedStateOf { pairColorGroup.toColor() } }

        val universityNews by viewModel.universityNews.collectAsStateWithLifecycle(emptyList())
        val deanNews by viewModel.deanNews.collectAsStateWithLifecycle(emptyList())
        var selectedNewsTab by rememberSaveable { mutableIntStateOf(0) }
        val currentNews = if (selectedNewsTab == 0) universityNews else deanNews

        LazyColumn(
            state = columnState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item(key = "updater") {
                InAppUpdateDialog(
                    state = updateState,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(Dimen.ContentPadding)
                )
            }

            item(key = "schedule_title") {
                HomeText(
                    text = favorite?.scheduleName
                        ?: stringResource(R.string.section_favorite_schedule),
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .clickable {
                            val currentSchedule = favorite
                            if (currentSchedule != null) {
                                navigateToSchedule(currentSchedule.id)
                            }
                        }
                        .padding(Dimen.ContentPadding * 2)
                )
            }

            item(key = "schedule_pager") {
                Stateful(
                    state = scheduleDays,
                    onSuccess = { days ->
                        if (days.isNotEmpty()) {
                            ScheduleHome(
                                days = days,
                                onLinkClicked = { BrowserUtils.openLink(context, it) },
                                colors = pairColors,
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .animateContentSize()
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.favorite_not_selected),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(
                                        horizontal = Dimen.ContentPadding,
                                        vertical = Dimen.ContentPadding * 2
                                    )
                            )
                        }
                    },
                    onLoading = {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(Dimen.ContentPadding * 2)
                        )
                    }
                )
            }

            item(key = "news_tabs") {
                Column(
                    modifier = Modifier.fillParentMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.section_news),
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.padding(
                            start = Dimen.ContentPadding * 2,
                            top = Dimen.ContentPadding * 2,
                            end = Dimen.ContentPadding * 2,
                            bottom = Dimen.ContentPadding
                        )
                    )
                    PrimaryTabRow(
                        selectedTabIndex = selectedNewsTab,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Tab(
                            selected = selectedNewsTab == 0,
                            onClick = { selectedNewsTab = 0 },
                            text = { Text(stringResource(R.string.news_university)) }
                        )
                        Tab(
                            selected = selectedNewsTab == 1,
                            onClick = { selectedNewsTab = 1 },
                            text = { Text(stringResource(R.string.news_dean)) }
                        )
                    }
                }
            }

            items(
                count = HomeViewModel.NEWS_COUNT,
                key = { "news_$selectedNewsTab$it" }
            ) { index ->
                NewsPost(
                    post = currentNews.getOrNull(index),
                    imageLoader = imageLoader,
                    onClick = {
                        navigateToNewsPost(it)
                    },
                    modifier = Modifier.padding(8.dp)
                )
                HorizontalDivider()
            }

            item(key = "more_news") {
                Box(
                    modifier = Modifier.fillParentMaxWidth()
                ) {
                    TextButton(
                        onClick = navigateToNews,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(text = stringResource(R.string.more_news))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 18.sp),
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null
        )
    }
}
