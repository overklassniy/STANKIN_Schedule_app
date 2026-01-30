package com.overklassniy.stankinschedule.journal.login.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.overklassniy.stankinschedule.journal.login.ui.R

/**
 * Верхняя панель экрана входа: заголовок и поддержка скролл-поведения.
 *
 * @param modifier Модификатор.
 * @param title Текст заголовка.
 * @param scrollBehavior Поведение скролла для TopAppBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginToolBar(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.journal_login_title),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}