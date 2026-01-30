package com.overklassniy.stankinschedule.news.review.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.news.review.ui.R

/**
 * Верхняя панель раздела «Обзор новостей».
 *
 * Отображает заголовок и поддерживает поведение прокрутки.
 *
 * @param modifier Модификатор для внешнего оформления.
 * @param title Заголовок панели.
 * @param scrollBehavior Поведение прокрутки TopAppBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("Unused")
fun NewsReviewToolBar(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.news_review_title),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}