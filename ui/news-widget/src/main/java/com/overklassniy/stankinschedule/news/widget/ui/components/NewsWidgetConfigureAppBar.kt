package com.overklassniy.stankinschedule.news.widget.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.overklassniy.stankinschedule.core.ui.components.BackButton
import com.overklassniy.stankinschedule.news.widget.ui.R

/**
 * Верхняя панель конфигурации виджета новостей.
 *
 * @param onBackPressed Обработчик возврата назад.
 * @param scrollBehavior Поведение прокрутки панели.
 * @param modifier Модификатор внешнего вида и расположения.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsWidgetConfigureAppBar(
    onBackPressed: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.news_widget_configure_title)) },
        navigationIcon = { BackButton(onClick = onBackPressed) },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}
