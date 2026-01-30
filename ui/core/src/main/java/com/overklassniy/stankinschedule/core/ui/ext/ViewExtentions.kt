package com.overklassniy.stankinschedule.core.ui.ext

import android.view.View

/**
 * Устанавливает видимость [View].
 *
 * @param visible Если true — видимо ([View.VISIBLE]), иначе скрыто ([View.GONE]).
 */
fun View.setVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}