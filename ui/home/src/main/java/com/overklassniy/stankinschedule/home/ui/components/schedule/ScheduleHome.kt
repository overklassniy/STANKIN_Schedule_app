package com.overklassniy.stankinschedule.home.ui.components.schedule

import android.widget.Toast
import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.home.ui.R
import com.overklassniy.stankinschedule.schedule.core.ui.PairColors
import com.overklassniy.stankinschedule.schedule.core.ui.ScheduleDayCard
import com.overklassniy.stankinschedule.schedule.core.ui.toColor
import com.overklassniy.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleHome(
    days: List<ScheduleViewDay>,
    onLinkClicked: (url: String) -> Unit,
    modifier: Modifier = Modifier,
    colors: PairColors = PairColorGroup.default().toColor()
) {
    val pagerState = rememberPagerState(
        initialPage = (days.size - 1) / 2,
        pageCount = { days.size }
    )
    val isScrolling = remember(pagerState) {
        derivedStateOf { pagerState.currentPageOffsetFraction != 0f }
    }


    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val clipboardScope = rememberCoroutineScope()

    Column(modifier = modifier) {

        SchedulePagerIndicator(
            state = pagerState,
            itemsCount = days.size,
            indicatorColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            ScheduleDayCard(
                scheduleDay = days[page],
                pairColors = colors,
                onPairClicked = {},
                onLinkClicked = onLinkClicked,
                onLinkCopied = {
                    clipboardScope.launch {
                        clipboard.setClipEntry(
                            ClipData.newPlainText(null, it).toClipEntry()
                        )
                    }
                    Toast.makeText(context, R.string.link_copied, Toast.LENGTH_SHORT).show()
                },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.ContentPadding)
                    .wrapPagerHeight(page, pagerState, isScrolling.value),
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.wrapPagerHeight(
    page: Int,
    pagerState: PagerState,
    isScrolling: Boolean
): Modifier {
    return if (pagerState.currentPage == page || isScrolling) {
        this.wrapContentHeight()
    } else {
        this.requiredHeightIn(max = 100.dp)
    }
}
