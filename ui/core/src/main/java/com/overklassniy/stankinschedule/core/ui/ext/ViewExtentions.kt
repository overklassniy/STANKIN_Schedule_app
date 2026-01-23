package com.overklassniy.stankinschedule.core.ui.ext

import android.view.View

fun View.setVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}