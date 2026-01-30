package com.overklassniy.stankinschedule.home.ui.data

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.totalBytesToDownload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Реализация In‑App Update через Google Play Core.
 *
 * Регистрирует слушатель состояния установки и предоставляет методы
 * проверки, запуска и завершения обновления.
 *
 * @param context Контекст приложения для создания [AppUpdateManagerFactory].
 */
class GooglePlayInAppUpdater(
    context: Context,
) : InAppUpdater, InstallStateUpdatedListener {

    private val appUpdater = AppUpdateManagerFactory.create(context)

    private val _updateState = MutableStateFlow<UpdateState?>(null)
    override val updateState: StateFlow<UpdateState?> = _updateState.asStateFlow()

    init {
        appUpdater.registerListener(this)
    }

    /**
     * Проверяет доступность обновления и обновляет [updateState].
     *
     * Устанавливает UpdateRequired при наличии подходящего обновления,
     * иначе — UpToDate.
     */
    override suspend fun checkUpdate() {
        try {
            val updateResult = appUpdater.appUpdateInfo.await()

            val stalenessDays = updateResult.clientVersionStalenessDays()
            if (updateResult.isUpdateAvailability(stalenessDays)) {
                _updateState.value = UpdateState.UpdateRequired(updateResult)
                return
            }
        } catch (_: Exception) {

        }

        _updateState.value = UpdateState.UpToDate
    }

    /**
     * Запускает процесс обновления через ActivityResult.
     *
     * @param info Информация об обновлении [AppUpdateInfo].
     * @param launcher Лаунчер для запуска IntentSender.
     * @param options Опции типа обновления [AppUpdateOptions].
     */
    override fun startUpdate(
        info: AppUpdateInfo,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        options: AppUpdateOptions
    ) {
        appUpdater.startUpdateFlowForResult(info, launcher, options)
    }

    /**
     * Обработчик состояния установки: обновляет прогресс и флаг перезапуска.
     *
     * @param state Текущее состояние установки.
     */
    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus == InstallStatus.DOWNLOADING) {
            val progress = state.bytesDownloaded / state.totalBytesToDownload.toFloat()
            _updateState.value = UpdateState.UpdateProgress(progress)
        }
        if (state.installStatus == InstallStatus.DOWNLOADED) {
            _updateState.value = UpdateState.UpdateRestart
        }
    }

    /**
     * Откладывает обновление: переводит состояние в UpToDate.
     */
    override fun later() {
        _updateState.value = UpdateState.UpToDate
    }

    /**
     * Завершает установку скачанного обновления.
     */
    override fun completeUpdate() {
        appUpdater.completeUpdate()
    }

    /**
     * Освобождает ресурсы: снимает регистрацию слушателя.
     */
    override fun onDestroy() {
        appUpdater.unregisterListener(this)
    }

    /**
     * Проверяет, подходит ли обновление для гибкого сценария.
     *
     * @param stalenessDays Кол-во дней устаревания установленной версии.
     * @return true, если обновление доступно и разрешён Flexible тип.
     */
    private fun AppUpdateInfo.isUpdateAvailability(stalenessDays: Int?): Boolean {
        return updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                && stalenessDays != null
                && stalenessDays >= DAYS_FOR_FLEXIBLE_UPDATE
    }

    /**
     * Ожидает завершения [Task] и возвращает результат или бросает исключение.
     *
     * @return Результат задачи.
     * @throws Exception если задача завершилась с ошибкой.
     */
    private suspend fun <T> Task<T>.await(): T {
        return suspendCoroutine { continuation ->
            addOnSuccessListener { result ->
                continuation.resume(result)
            }
            addOnFailureListener { error ->
                continuation.resumeWithException(error)
            }
        }
    }

    companion object {

        /**
         * Минимальное количество дней устаревания для гибкого обновления.
         */
        const val DAYS_FOR_FLEXIBLE_UPDATE = 7
    }
}