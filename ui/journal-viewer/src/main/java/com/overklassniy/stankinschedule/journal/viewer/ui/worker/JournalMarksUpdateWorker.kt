package com.overklassniy.stankinschedule.journal.viewer.ui.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.overklassniy.stankinschedule.core.ui.notification.NotificationUtils
import com.overklassniy.stankinschedule.core.ui.utils.formatSemesterName
import com.overklassniy.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.overklassniy.stankinschedule.journal.core.domain.model.JournalSemester
import com.overklassniy.stankinschedule.journal.core.domain.usecase.JournalUpdateUseCase
import com.overklassniy.stankinschedule.journal.viewer.ui.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Фоновый воркер обновления оценок и семестров журнала.
 *
 * Периодически проверяет новые семестры и изменения оценок, формирует
 * соответствующие уведомления и выполняется в foreground режиме.
 */
@HiltWorker
class JournalMarksUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val useCase: JournalUpdateUseCase
) : CoroutineWorker(context, workerParameters) {

    /**
     * Основная работа воркера: обновление семестров и оценок, отправка уведомлений.
     *
     * @return Result.success при успешной обработке; Result.failure при потере авторизации;
     * Result.retry при прочих ошибках.
     */
    override suspend fun doWork(): Result {
        return try {
            val manager = NotificationManagerCompat.from(applicationContext)
            val (student, newSemesters) = useCase.updateSemesters()
            if (newSemesters.isNotEmpty()) {
                sendSemestersNotification(manager, newSemesters)
            }

            if (student != null) {
                val lastSemester = student.semesters.firstOrNull()
                if (lastSemester != null) {
                    val newMarks = useCase.updateSemesterMarks(lastSemester)
                    if (newMarks.isNotEmpty()) {
                        sendMarksNotification(manager, newMarks)
                    }
                }
            }

            Result.success()

        } catch (_: StudentAuthorizedException) {
            // Токен истек: тихо прекращаем фоновые обновления.
            // После повторного входа пользователя воркер запустится снова.
            cancelWorker(applicationContext)
            Result.failure()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    private fun getString(@StringRes id: Int, vararg args: Any): String {
        return applicationContext.getString(id, args)
    }

    /**
     * Возвращает информацию для выполнения воркера в foreground, включая уведомление.
     */
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationUtils.createModuleJournalNotification(applicationContext)
            .setContentTitle(getString(R.string.journal_notification))
            .setContentText(getString(R.string.journal_notification_update))
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_journal_notification)
            .setAutoCancel(true)
            .build()

        val notificationId = NotificationUtils.MODULE_JOURNAL_IDS

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    /**
     * Отправляет уведомление о появлении новых семестров.
     *
     * @param manager Менеджер уведомлений.
     * @param newSemesters Набор новых семестров.
     */
    private fun sendSemestersNotification(
        manager: NotificationManagerCompat,
        newSemesters: Set<JournalSemester>
    ) {
        val semesterNames = newSemesters.joinToString(", ") {
            formatSemesterName(applicationContext, it.course, it.semester)
        }
        val content = getString(R.string.journal_new_semester) + semesterNames
        val notification = NotificationUtils.createModuleJournalNotification(applicationContext)
            .setContentTitle(getString(R.string.journal_notification))
            .setContentText(content)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_journal_notification)
            .setAutoCancel(true)
            .build()

        NotificationUtils.notify(
            context = applicationContext,
            manager = manager,
            id = NotificationUtils.MODULE_JOURNAL_IDS + 100,
            notification = notification
        )
    }

    /**
     * Отправляет уведомление об изменении оценок.
     *
     * @param manager Менеджер уведомлений.
     * @param newMarks Набор строк с изменёнными оценками.
     */
    private fun sendMarksNotification(
        manager: NotificationManagerCompat,
        newMarks: Set<String>
    ) {
        val content = getString(R.string.journal_new_marks) + "\n" + newMarks.joinToString("\n")
        val notification = NotificationUtils.createModuleJournalNotification(applicationContext)
            .setContentTitle(getString(R.string.journal_notification))
            .setContentText(content)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_journal_notification)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
                    .setBigContentTitle(getString(R.string.journal_notification))
            )
            .setAutoCancel(true)
            .build()

        NotificationUtils.notify(
            context = applicationContext,
            manager = manager,
            id = NotificationUtils.MODULE_JOURNAL_IDS + 200,
            notification = notification
        )
    }

    companion object {

        private const val TAG = "JournalMarksUpdateWorker"

        /**
         * Запускает периодический воркер обновления журнала.
         * Интервал — каждые 2 часа, сеть обязательна, политика — KEEP.
         */
        fun startWorker(context: Context) {
            val manager = WorkManager.getInstance(context)

            val workerClass = JournalMarksUpdateWorker::class.java
            val worker = PeriodicWorkRequest.Builder(workerClass, 2, TimeUnit.HOURS)
                .addTag(TAG)
                .setInitialDelay(1, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            manager.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, worker)
        }

        /**
         * Отменяет все задачи воркера по тегу.
         */
        fun cancelWorker(context: Context) {
            val manager = WorkManager.getInstance(context)
            manager.cancelAllWorkByTag(TAG)
        }
    }
}
