package com.overklassniy.stankinschedule.news.review.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.overklassniy.stankinschedule.core.domain.ext.formatDate
import com.overklassniy.stankinschedule.core.ui.theme.AppTheme
import com.overklassniy.stankinschedule.core.ui.theme.Dimen
import com.overklassniy.stankinschedule.core.ui.utils.newsImageLoader
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost

@Preview(showBackground = true)
@Composable
fun NewsPostPreview() {
    AppTheme {
        NewsPost(
            post = NewsPost(
                0, "Example title.", "", "07.07.22", ""
            ),
            imageLoader = newsImageLoader(LocalContext.current),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

/**
 * Карточка новости.
 *
 * Показывает превью изображения, заголовок и дату. При отсутствии данных рендерит холдер-заглушку.
 *
 * @param post Модель новости или null для отображения заглушки.
 * @param imageLoader Загрузчик изображений.
 * @param onClick Обработчик клика по карточке новости.
 * @param modifier Модификатор для внешнего оформления.
 */
@Composable
fun NewsPost(
    post: NewsPost?,
    imageLoader: ImageLoader,
    onClick: (post: NewsPost) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (post != null) {
        NewsPostContent(
            post = post,
            imageLoader = imageLoader,
            onClick = onClick,
            modifier = modifier
        )
    } else {
        NewsPostHolder(modifier = modifier)
    }
}

/**
 * Заглушка карточки новости, используемая, когда данных ещё нет.
 *
 * @param modifier Модификатор.
 */
@Composable
private fun NewsPostHolder(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding)
    ) {
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .size(100.dp, 56.dp)
                .align(Alignment.CenterVertically)
        )
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .weight(weight = 1f)
                .height(56.dp / 3)
        )
    }
}

/**
 * Содержимое карточки новости.
 *
 * @param post Модель новости.
 * @param imageLoader Загрузчик изображений.
 * @param onClick Обработчик клика по карточке.
 * @param modifier Модификатор.
 */
@Composable
private fun NewsPostContent(
    post: NewsPost,
    imageLoader: ImageLoader,
    onClick: (post: NewsPost) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onClick(post) })
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding)
    ) {
        AsyncImage(
            model = post.previewImageUrl,
            imageLoader = imageLoader,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp, 56.dp)
                .align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .defaultMinSize(minHeight = 56.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = post.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Text(
                text = formatDate(post.date),
                modifier = Modifier
                    .align(Alignment.End)
            )
        }
    }
}