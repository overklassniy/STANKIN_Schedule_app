package com.overklassniy.stankinschedule.core.ui.notification

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * Утилиты для работы с системными уведомлениями: проверка разрешений и фабрики билдера для стандартных каналов.
 */
object NotificationUtils {
    const val CHANNEL_COMMON = "channel_common"
    const val CHANNEL_MODULE_JOURNAL = "channel_module_journal"
    const val MODULE_JOURNAL_IDS: Int = 1_000_000

    /**
     * Проверяет состояние разрешения на уведомления.
     *
     * Для Android 13+ возвращает true, если разрешение POST_NOTIFICATIONS не выдано;
     * для более ранних версий всегда true.
     *
     * @param context Контекст приложения.
     * @return Результат проверки разрешения: см. описание выше.
     */
    fun isNotificationAllow(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @kotlin.jvm.Throws(SecurityException::class)
            /**
             * Публикует уведомление с проверкой разрешений.
             *
             * @param context Контекст.
             * @param manager Менеджер уведомлений.
             * @param id Идентификатор уведомления.
             * @param notification Готовое уведомление.
             * @throws SecurityException при запрете публикации системой.
             */
    fun notify(
        context: Context,
        manager: NotificationManagerCompat,
        id: Int,
        notification: Notification
    ) {
        manager.areNotificationsEnabled()
        if (isNotificationAllow(context)) {
            manager.notify(id, notification)
        }
    }

    /**
     * Создаёт NotificationCompat.Builder для общего канала.
     *
     * @param context Контекст.
     * @return Билдер с каналом CHANNEL_COMMON и DEFAULT_ALL.
     */
    @JvmStatic
    fun createCommonNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat
            .Builder(context, CHANNEL_COMMON)
            .setDefaults(Notification.DEFAULT_ALL)
    }

    /**
     * Создаёт NotificationCompat.Builder для канала модуля «Journal».
     *
     * @param context Контекст.
     * @return Билдер с каналом CHANNEL_MODULE_JOURNAL и DEFAULT_ALL.
     */
    @JvmStatic
    fun createModuleJournalNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat
            .Builder(context, CHANNEL_MODULE_JOURNAL)
            .setDefaults(Notification.DEFAULT_ALL)
    }
}