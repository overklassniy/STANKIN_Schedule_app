package com.overklassniy.stankinschedule.schedule.table.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.overklassniy.stankinschedule.core.ui.components.BackButton
import com.overklassniy.stankinschedule.schedule.table.ui.R

/**
 * Верхняя панель экрана таблицы расписания.
 *
 * Формирует UI: TopAppBar с заголовком и кнопкой назад. Во второй строке
 * отображает имя расписания, если оно не пустое.
 *
 * @param scheduleName Имя расписания. Пустая строка скрывает вторую строку заголовка.
 * @param onBackClicked Обработчик клика по кнопке назад.
 * @param modifier Модификатор внешнего вида и расположения.
 * @param scrollBehavior Поведение прокрутки панели. Влияет на подъем и скрытие.
 * @return Ничего не возвращает. Побочных эффектов нет.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTableAppBar(
    scheduleName: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(R.string.table_view_title),
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                AnimatedVisibility(
                    visible = scheduleName.isNotEmpty()
                ) {
                    Text(
                        text = scheduleName,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        },
        navigationIcon = {
            BackButton(onClick = onBackClicked)
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}