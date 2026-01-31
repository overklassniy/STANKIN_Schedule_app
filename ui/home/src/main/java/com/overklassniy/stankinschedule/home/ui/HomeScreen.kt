package com.overklassniy.stankinschedule.home.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.overklassniy.stankinschedule.news.review.ui.components.AppTabIndicator
import com.overklassniy.stankinschedule.news.review.ui.components.NewsPost
import com.overklassniy.stankinschedule.news.review.ui.components.pagerTabIndicatorOffset
import com.overklassniy.stankinschedule.schedule.core.ui.toColor
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import kotlinx.coroutines.launch

/**
 * Главный экран приложения: верхняя панель, избранное расписание и вкладки новостей.
 *
 * @param viewModel ViewModel экрана, источник состояния UI и данных.
 * @param navigateToSchedule Навигация к расписанию по идентификатору.
 * @param navigateToNews Переход к полному разделу «Новости».
 * @param navigateToNewsPost Переход к конкретной карточке новости.
 * @param navigateToSettings Открыть экран настроек.
 * @param modifier Модификатор для внешнего позиционирования.
 * @param imageLoader Загрузчик изображений для карточек новостей.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

    val hasAppUpdate by viewModel.hasUpdate.collectAsStateWithLifecycle()

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
                        BadgedBox(
                            badge = {
                                if (hasAppUpdate) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_settings),
                                contentDescription = null
                            )
                        }
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
        val announcementsNews by viewModel.announcementsNews.collectAsStateWithLifecycle(emptyList())
        val deanNews by viewModel.deanNews.collectAsStateWithLifecycle(emptyList())

        val pagerState = rememberPagerState(
            pageCount = { 3 } // Университет, Анонсы, Деканат
        )
        val pagerScope = rememberCoroutineScope()
        val tabRowHeight = 48.dp

        val newsLists = listOf(universityNews, announcementsNews, deanNews)

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
                        selectedTabIndex = pagerState.currentPage,
                        indicator = {
                            AppTabIndicator(
                                modifier = Modifier.pagerTabIndicatorOffset(this, pagerState)
                            )
                        },
                        divider = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = tabRowHeight)
                    ) {
                        Tab(
                            selected = pagerState.currentPage == 0,
                            onClick = {
                                pagerScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            text = { Text(stringResource(R.string.news_university)) }
                        )
                        Tab(
                            selected = pagerState.currentPage == 1,
                            onClick = {
                                pagerScope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            text = { Text(stringResource(R.string.news_announcements)) }
                        )
                        Tab(
                            selected = pagerState.currentPage == 2,
                            onClick = {
                                pagerScope.launch {
                                    pagerState.animateScrollToPage(2)
                                }
                            },
                            text = { Text(stringResource(R.string.news_dean)) }
                        )
                    }

                    // Pager is placed directly under TabRow to avoid layout gaps
                    HorizontalPager(
                        state = pagerState,
                        key = { it },
                        beyondViewportPageCount = 1,
                        pageSpacing = 0.dp,
                        contentPadding = PaddingValues(0.dp),
                        verticalAlignment = Alignment.Top,
                        userScrollEnabled = true,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        val currentNews = newsLists[page]
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            // Показываем либо NEWS_COUNT элементов, либо сколько есть (с placeholder'ами для загрузки)
                            val itemCount = if (currentNews.isEmpty()) {
                                // Если данные еще загружаются, показываем placeholder'ы
                                HomeViewModel.NEWS_COUNT
                            } else {
                                // Показываем только реальные данные
                                currentNews.size.coerceAtMost(HomeViewModel.NEWS_COUNT)
                            }
                            repeat(itemCount) { index ->
                                val post = currentNews.getOrNull(index)
                                val itemModifier = if (index == 0) {
                                    Modifier.padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        top = 0.dp,
                                        bottom = 8.dp
                                    )
                                } else {
                                    Modifier.padding(8.dp)
                                }
                                NewsPost(
                                    post = post,
                                    imageLoader = imageLoader,
                                    onClick = { clicked ->
                                        navigateToNewsPost(clicked)
                                    },
                                    modifier = itemModifier
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
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

/**
 * Заголовок секции на главном экране с текстом и стрелкой навигации.
 *
 * @param text Отображаемый текст заголовка.
 * @param modifier Модификатор.
 */
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
