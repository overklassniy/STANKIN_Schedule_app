package com.overklassniy.stankinschedule.core.domain.usecase

import com.overklassniy.stankinschedule.core.domain.repository.DeviceRepository
import javax.inject.Inject

/**
 * Use case для работы с файлами на устройстве.
 */
class DeviceUseCase @Inject constructor(
    private val device: DeviceRepository
) {

    /**
     * Извлекает имя файла из пути.
     *
     * @param path Путь к файлу
     * @param removeExtension true, если нужно удалить расширение файла
     * @return Имя файла
     * @throws IllegalArgumentException если имя файла не может быть извлечено
     */
    fun extractFilename(path: String, removeExtension: Boolean = false): String {
        val filename = device.extractFilename(path)
            ?: throw IllegalArgumentException("Can't extract filename")

        if (removeExtension) {
            return filename.substringBeforeLast('.')
        }

        return filename
    }
}