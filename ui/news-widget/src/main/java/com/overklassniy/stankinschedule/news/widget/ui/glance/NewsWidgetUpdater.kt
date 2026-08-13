package com.overklassniy.stankinschedule.news.widget.ui.glance

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Утилита для обновления всех экземпляров виджета новостей.
 *
 * Вызывается из других модулей (например, из ui/news-review после обновления новостей)
 * для принудительной перерисовки виджетов.
 */
object NewsWidgetUpdater {

    /**
     * Асинхронно обновляет все экземпляры виджета новостей.
     *
     * @param context Контекст приложения.
     */
    fun updateAll(context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                NewsGlanceWidget().updateAll(context)
            } catch (e: Exception) {
                android.util.Log.e("NewsWidgetUpdater", "updateAll failed", e)
            }
        }
    }
}