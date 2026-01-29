package com.overklassniy.stankinschedule.core.domain.repository

/**
 * Интерфейс репозитория для работы с данными устройства и файловой системой.
 */
interface DeviceRepository {

    /**
     * Извлекает имя файла из полного пути к нему.
     *
     * @param path Полный путь к файлу.
     * @return Имя файла или null, если не удалось извлечь.
     */
    fun extractFilename(path: String): String?
}