package com.overklassniy.stankinschedule.schedule.repository.ui.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.StringRes
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.overklassniy.stankinschedule.core.ui.notification.NotificationUtils
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem
import com.overklassniy.stankinschedule.schedule.repository.domain.usecase.RepositoryLoaderUseCase
import com.overklassniy.stankinschedule.schedule.repository.ui.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.joda.time.DateTimeUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.overklassniy.stankinschedule.core.ui.R as R_core

/**
 * Фоновый загрузчик расписаний (WorkManager).
 *
 * Загружает файл расписания или сохраняет его локально и сообщает прогресс через foreground-уведомление.
 *
 * Примечания:
 * - Использует expedited work (при дефиците квоты падает в обычный режим).
 * - Требует сети (NetworkType.CONNECTED).
 */
@HiltWorker
class ScheduleDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val loaderUseCase: RepositoryLoaderUseCase
) : CoroutineWorker(context, workerParameters) {

    /**
     * Точка входа выполнения задачи.
     *
     * Алгоритм:
     *  1. Читает входные данные (имя, путь, категория, режим).
     *  2. Запускает downloadOnly или полноценную загрузку.
     *  3. Возвращает Result.success с выходными данными.
     *
     * @return [Result] Результат выполнения Work.
     */
    override suspend fun doWork(): Result {

        val scheduleName = inputData.getString(SCHEDULE_NAME)!!
        val schedulePath = inputData.getString(SCHEDULE_PATH)!!
        val scheduleCategory = inputData.getString(SCHEDULE_CATEGORY)!!
        val downloadOnly = inputData.getBoolean(SCHEDULE_DOWNLOAD_ONLY, true)

        return if (downloadOnly) {
            val filePath = downloadOnly(scheduleCategory, schedulePath, scheduleName)
            Result.success(
                Data.Builder()
                    .putString(OUTPUT_SCHEDULE_NAME, scheduleName)
                    .putString(OUTPUT_FILE_PATH, filePath)
                    .build()
            )
        } else {
            download(scheduleCategory, schedulePath, scheduleName, false)
            Result.success(
                Data.Builder()
                    .putString(OUTPUT_SCHEDULE_NAME, scheduleName)
                    .build()
            )
        }
    }

    /**
     * Предоставляет ForegroundInfo для отображения прогресса через уведомление.
     *
     * @return [ForegroundInfo] Конфигурация foreground-уведомления для WorkManager.
     */
    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo(
            scheduleName = inputData.getString(SCHEDULE_NAME) ?: "",
            notificationId = createID()
        )
    }

    /**
     * Создаёт ForegroundInfo с уведомлением о загрузке расписания.
     *
     * @param scheduleName Заголовок уведомления.
     * @param notificationId Идентификатор уведомления.
     *
     * @return [ForegroundInfo] Данные для запуска foreground-сервиса.
     *
     * Примечания:
     *     - На Android Q+ указывается тип FOREGROUND_SERVICE_TYPE_DATA_SYNC.
     */
    private fun createForegroundInfo(scheduleName: String, notificationId: Int): ForegroundInfo {
        val cancel = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        val notification = NotificationUtils.createCommonNotification(applicationContext)
            .setContentTitle(scheduleName)
            .setTicker(scheduleName)
            .setSmallIcon(R.drawable.ic_notification_file_download)
            .setWhen(DateTimeUtils.currentTimeMillis())
            .setProgress(100, 0, true)
            .addAction(R.drawable.ic_notification_cancel, getString(R_core.string.cancel), cancel)
            .build()

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
     * Полноценная загрузка расписания с сохранением в хранилище.
     *
     * @param scheduleCategory Категория репозитория.
     * @param schedulePath Относительный путь к файлу.
     * @param scheduleName Имя для сохранения расписания.
     * @param replaceExist Заменять существующую запись.
     */
    private suspend fun download(
        scheduleCategory: String,
        schedulePath: String,
        scheduleName: String,
        replaceExist: Boolean
    ) {
        setProgress(
            data = Data.Builder()
                .putString(OUTPUT_SCHEDULE_NAME, scheduleName)
                .build()
        )
        loaderUseCase.loadSchedule(scheduleCategory, schedulePath, scheduleName, replaceExist)
    }

    /**
     * Скачивает только файл расписания без сохранения в БД.
     *
     * @param scheduleCategory: Категория.
     * @param schedulePath: Путь.
     * @param scheduleName: Имя.
     *
     * @return [String] Абсолютный путь к скачанному файлу.
     */
    private suspend fun downloadOnly(
        scheduleCategory: String,
        schedulePath: String,
        scheduleName: String
    ): String {
        setProgress(
            data = Data.Builder()
                .putString(OUTPUT_SCHEDULE_NAME, scheduleName)
                .build()
        )
        return loaderUseCase.downloadScheduleFile(scheduleCategory, schedulePath, scheduleName)
    }

    /**
     * Получает строковый ресурс из контекста приложения.
     *
     * @param id Идентификатор ресурса.
     *
     * @return [String] Значение строкового ресурса.
     */
    private fun getString(@StringRes id: Int): String {
        return applicationContext.getString(id)
    }

    /**
     * Генерирует идентификатор уведомления на основе текущего времени.
     *
     * @return [Int] Уникальный идентификатор уведомления.
     */
    private fun createID(): Int {
        return SimpleDateFormat("ddHHmmss", Locale.US).format(Date()).toInt()
    }

    companion object {

        const val WORKER_TAG = "schedule_download_worker_tag"

        private const val SCHEDULE_DOWNLOAD_ONLY = "schedule_download_only"
        private const val SCHEDULE_NAME = "schedule_save_name"
        private const val SCHEDULE_PATH = "schedule_path"
        private const val SCHEDULE_CATEGORY = "schedule_category"

        const val OUTPUT_SCHEDULE_NAME = "scheduleName"
        const val OUTPUT_FILE_PATH = "filePath"

        /**
         * Запускает уникальный worker для загрузки расписания.
         *
         * @param context Context приложения.
         * @param scheduleName Имя для сохранения расписания.
         * @param item Репозитный элемент (путь, категория).
         * @param downloadOnly Только скачать файл без сохранения в БД.
         *
         * @return [String] Уникальное имя задачи для отслеживания статуса.
         *
         * Примечания:
         *     - Имя включает timestamp для предотвращения коллизий.
         */
        fun startWorker(
            context: Context,
            scheduleName: String,
            item: RepositoryItem,
            downloadOnly: Boolean = true,
        ): String {
            val manager = WorkManager.getInstance(context)

            // Уникальное имя worker'а с timestamp для избежания конфликтов при повторных загрузках
            val timestamp = System.currentTimeMillis()
            val workerName = "ScheduleWorker-${item.name}-${item.category}-$timestamp"

            val worker = OneTimeWorkRequest.Builder(ScheduleDownloadWorker::class.java)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    Data.Builder()
                        .putString(SCHEDULE_NAME, scheduleName)
                        .putString(SCHEDULE_PATH, item.path)
                        .putString(SCHEDULE_CATEGORY, item.category)
                        .putBoolean(SCHEDULE_DOWNLOAD_ONLY, downloadOnly)
                        .build()
                )
                .addTag(WORKER_TAG)
                .build()

            manager.enqueueUniqueWork(workerName, ExistingWorkPolicy.REPLACE, worker)

            return workerName
        }
    }
}