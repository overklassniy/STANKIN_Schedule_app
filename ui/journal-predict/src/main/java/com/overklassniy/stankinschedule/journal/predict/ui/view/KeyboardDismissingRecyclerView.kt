package com.overklassniy.stankinschedule.journal.predict.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView, который скрывает клавиатуру при начале прокрутки.
 *
 * @constructor Создаёт вид с возможностью скрытия IME.
 */
class KeyboardDismissingRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0,
) : RecyclerView(context, attrs, defStyleAttrs) {

    private var scrollListener: OnScrollListener? = null

    private val inputMethodManager = ContextCompat.getSystemService(
        context, InputMethodManager::class.java
    )

    init {
        scrollListener = object : OnScrollListener() {
            var isKeyboardDismissedByScroll = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, state: Int) {
                when (state) {
                    SCROLL_STATE_DRAGGING -> if (!isKeyboardDismissedByScroll) {
                        hideKeyboard()
                        isKeyboardDismissedByScroll = !isKeyboardDismissedByScroll
                    }

                    SCROLL_STATE_IDLE -> isKeyboardDismissedByScroll = false
                }
            }
        }
    }

    /**
     * Регистрирует слушатель прокрутки при добавлении в окно.
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        scrollListener?.let { addOnScrollListener(it) }
    }

    /**
     * Удаляет слушатель прокрутки при удалении из окна.
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scrollListener?.let { removeOnScrollListener(it) }
    }

    /**
     * Скрывает системную клавиатуру и снимает фокус.
     */
    private fun hideKeyboard() {
        inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
        clearFocus()
    }
}