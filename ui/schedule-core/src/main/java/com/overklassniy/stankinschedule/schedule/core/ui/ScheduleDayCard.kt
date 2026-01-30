package com.overklassniy.stankinschedule.schedule.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.overklassniy.stankinschedule.core.ui.ext.toTitleCase
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewDay
import com.overklassniy.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair

/**
 * Компонент карточки дня расписания.
 *
 * Формирует заголовок дня и список пар. При отсутствии пар показывает текст-заглушку.
 *
 * @param scheduleDay Модель дня расписания, содержит дату и список пар.
 * @param pairColors Цветовая схема для типов пар и подгрупп.
 * @param onPairClicked Обработчик клика по паре (передаётся модель пары).
 * @param onLinkClicked Обработчик клика по ссылке аудитории.
 * @param onLinkCopied Обработчик долгого нажатия по ссылке аудитории (копирование).
 * @param modifier Внешний модификатор.
 * @param enabled Признак активных кликов по карточкам пар.
 */
@Composable
fun ScheduleDayCard(
    scheduleDay: ScheduleViewDay,
    pairColors: PairColors,
    onPairClicked: (pair: ScheduleViewPair) -> Unit,
    onLinkClicked: (link: String) -> Unit,
    onLinkCopied: (link: String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {

    Column(
        modifier = modifier
    ) {
        Text(
            text = scheduleDay.day.toString("EEEE, dd MMMM").toTitleCase(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.ContentPadding)
        )

        // Если список пар пуст – показываем локализованную заглушку вместо списка
        if (scheduleDay.pairs.isEmpty()) {

            Text(
                text = stringResource(R.string.schedule_no_pairs),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.ContentPadding)
            )
        }

        scheduleDay.pairs.forEach { pair ->
            // key обеспечивает стабильность элементов списка для Compose и корректные анимации
            key(pair.id) {
                PairCard(
                    pair = pair,
                    pairColors = pairColors,
                    onClicked = { onPairClicked(pair) },
                    enabled = enabled,
                    onLinkClicked = onLinkClicked,
                    onLinkCopied = onLinkCopied,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.ContentPadding)
                )
            }
        }
    }
}