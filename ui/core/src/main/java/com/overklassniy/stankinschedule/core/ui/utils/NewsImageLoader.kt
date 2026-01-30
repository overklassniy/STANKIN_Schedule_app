package com.overklassniy.stankinschedule.core.ui.utils

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.imageLoader
import com.overklassniy.stankinschedule.core.ui.R

/**
 * Создаёт ImageLoader для новостных изображений с кэшированием и плейсхолдером.
 *
 * @param context Контекст приложения.
 * @param cacheName Имя директории кэша в cacheDir.
 * @return Настроенный Coil ImageLoader.
 */
fun newsImageLoader(
    context: Context,
    cacheName: String = "image_cache",
): ImageLoader {
    return context.imageLoader.newBuilder()
        .crossfade(true)
        .crossfade(300)
        .placeholder(R.drawable.news_preview_placeholder)
        .diskCache(
            DiskCache.Builder()
                .directory(context.cacheDir.resolve(cacheName))
                .maximumMaxSizeBytes(1024 * 1024 * 64)
                .build()
        )
        .build()
}