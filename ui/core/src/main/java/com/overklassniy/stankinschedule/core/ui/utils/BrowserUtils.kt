package com.overklassniy.stankinschedule.core.ui.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
import android.net.Uri
import android.os.Build
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.overklassniy.stankinschedule.core.ui.R


/**
 * Утилиты для открытия ссылок: системный браузер/приложение и Chrome Custom Tabs.
 */
object BrowserUtils {

    /**
     * Открывает ссылку из строки.
     * Если в строке нет схемы (http/https), добавляется https://.
     *
     * @param context Контекст.
     * @param url URL-адрес (например "zoom.us").
     * @param includeApp Если true, сначала пробует открыть через приложение (CATEGORY_BROWSABLE), иначе сразу Custom Tabs.
     */
    fun openLink(context: Context, url: String, includeApp: Boolean = false) {
        val normalized = url.trim()
        val withScheme = when {
            normalized.isEmpty() -> normalized
            normalized.contains("://") -> normalized
            else -> "https://$normalized"
        }
        openLink(context, withScheme.toUri(), includeApp)
    }

    /**
     * Открывает ссылку из Uri.
     *
     * @param context Контекст.
     * @param uri Ссылка.
     * @param includeApp Если true, делает попытку открыть через приложение, в противном случае — через Custom Tabs.
     */
    fun openLink(context: Context, uri: Uri, includeApp: Boolean = false) {
        val scheme = uri.scheme
        if (scheme == null || scheme !in listOf("http", "https")) {
            return
        }
        if (includeApp) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                    flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_REQUIRE_NON_BROWSER
                    } else {
                        FLAG_ACTIVITY_NEW_TASK
                    }
                }
                context.startActivity(intent)
                return

            } catch (_: ActivityNotFoundException) {

            }
        }

        startCustomTabs(context, uri)
    }

    /**
     * Запускает Chrome Custom Tabs для указанной ссылки.
     * При отсутствии активности для Custom Tabs пробует обычный Intent.ACTION_VIEW.
     *
     * @param context Контекст.
     * @param uri Ссылка.
     */
    private fun startCustomTabs(context: Context, uri: Uri) {
        try {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                .setDefaultColorSchemeParams(
                    CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(context.getColor(R.color.onSecondary))
                        .build()
                )
                .build()

            customTabsIntent.launchUrl(context, uri)
        } catch (_: ActivityNotFoundException) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                // Нет приложения для открытия ссылки — игнорируем
            }
        }
    }
}