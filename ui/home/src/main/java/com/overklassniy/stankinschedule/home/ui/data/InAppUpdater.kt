package com.overklassniy.stankinschedule.home.ui.data

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateOptions
import kotlinx.coroutines.flow.StateFlow

/**
 * Интерфейс для реализации In‑App Update.
 *
 * Определяет поток состояния обновления и операции управления процессом.
 */
interface InAppUpdater {

    /**
     * Поток состояния обновления.
     *
     * Возможные значения см. в [UpdateState].
     */
    val updateState: StateFlow<UpdateState?>

    /**
     * Проверяет доступность обновления.
     */
    suspend fun checkUpdate()

    /**
     * Запускает процесс обновления через ActivityResult.
     *
     * @param info Информация об обновлении [AppUpdateInfo].
     * @param launcher Лаунчер для запуска IntentSender.
     * @param options Опции типа обновления [AppUpdateOptions].
     */
    fun startUpdate(
        info: AppUpdateInfo,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        options: AppUpdateOptions
    )

    /**
     * Откладывает обновление, переводя состояние в UpToDate.
     */
    fun later()

    /**
     * Завершает установку скачанного обновления.
     */
    fun completeUpdate()

    /**
     * Освобождает ресурсы/слушатели.
     */
    fun onDestroy()

}

/**
 * Состояния процесса In‑App Update.
 *
 * - UpdateRequired: доступно обновление, можно запустить.
 * - UpdateProgress: идёт загрузка, содержит прогресс.
 * - UpdateRestart: обновление скачано, требуется перезапуск.
 * - UpToDate: актуальная версия, обновлений нет.
 */
interface UpdateState {
    class UpdateRequired(val info: AppUpdateInfo) : UpdateState
    class UpdateProgress(val progress: Float) : UpdateState
    object UpdateRestart : UpdateState
    object UpToDate : UpdateState
}