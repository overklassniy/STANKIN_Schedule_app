package com.overklassniy.stankinschedule.core.data.cache

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import com.google.gson.GsonBuilder
import com.overklassniy.stankinschedule.core.data.mapper.DateTimeTypeConverter
import com.overklassniy.stankinschedule.core.domain.cache.CacheContainer
import dagger.hilt.android.qualifiers.ApplicationContext
import org.joda.time.DateTime
import java.io.File
import javax.inject.Inject

/**
 * Менеджер для работы с кэшем приложения.
 * Позволяет сохранять и загружать данные в JSON формате, используя Gson.
 *
 * @param context Контекст приложения
 */
class CacheManager @Inject constructor(
    @ApplicationContext context: Context,
) {

    // Проверка режима отладки для логирования ошибок
    private val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    // Директория для кэша
    private val cacheDir: File = context.cacheDir

    // Gson instance для сериализации/десериализации
    private var gson = GsonBuilder()
        .registerTypeAdapter(DateTime::class.java, DateTimeTypeConverter())
        .create()

    // Базовые пути для файлов кэша
    private val startedPaths = arrayListOf<String>()

    /**
     * Добавляет начальные пути к директории кэша.
     * Эти пути будут использоваться как префикс для всех файлов кэша.
     *
     * @param paths Список путей (директорий)
     */
    fun addStartedPath(vararg paths: String) {
        startedPaths.addAll(paths)
    }

    /**
     * Настраивает парсер Gson.
     * Позволяет добавить собственные адаптеры и настройки для Gson.
     *
     * @param parserBuilder Функция-расширение для настройки GsonBuilder
     */
    fun configurateParser(parserBuilder: GsonBuilder.() -> GsonBuilder) {
        gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, DateTimeTypeConverter())
            .parserBuilder()
            .create()
    }

    /**
     * Сохраняет данные в кэш.
     * Данные сохраняются в файл JSON по указанному пути.
     *
     * Алгоритм:
     * 1. Определяет файл по указанным путям.
     * 2. Создает CacheContainer с данными и текущим временем.
     * 3. Сериализует контейнер в JSON и записывает в файл.
     *
     * @param data Данные для сохранения
     * @param paths Путь к файлу (директории и имя файла)
     */
    fun saveToCache(data: Any, vararg paths: String) {
        try {
            fileFromPaths(paths).bufferedWriter().use { writer ->
                gson.toJson(CacheContainer(data, DateTime.now()), writer)
            }

        } catch (ignored: Exception) {
            if (isDebug) {
                Log.e(TAG, "saveToCache: ", ignored)
            }
        }
    }

    /**
     * Загружает данные из кэша.
     * Считывает данные из файла JSON и десериализует их в объект указанного типа.
     *
     * Алгоритм:
     * 1. Определяет файл по указанным путям.
     * 2. Проверяет существование файла.
     * 3. Считывает содержимое файла и десериализует в CacheObject.
     * 4. Если данные корректны, десериализует поле data в целевой тип T.
     * 5. Возвращает CacheContainer с данными и временем кэширования.
     *
     * @param type Класс типа данных, в который нужно десериализовать
     * @param paths Путь к файлу (директории и имя файла)
     * @return Контейнер с данными [CacheContainer] или null, если загрузка не удалась
     */
    fun <T : Any> loadFromCache(type: Class<T>, vararg paths: String): CacheContainer<T>? {
        try {
            val filePath = fileFromPaths(paths)
            if (!filePath.exists()) {
                return null
            }

            val raw = filePath.reader().use { reader ->
                gson.fromJson(reader, CacheObject::class.java)
            }

            return if (raw.data != null && raw.time != null) {
                CacheContainer(
                    data = gson.fromJson(raw.data, type),
                    cacheTime = raw.time
                )
            } else {
                null
            }

        } catch (ignored: Exception) {
            if (isDebug) {
                Log.e(TAG, "loadFromCache: ", ignored)
            }
        }

        return null
    }

    /**
     * Очищает весь кэш.
     * Удаляет все файлы и директории, созданные менеджером кэша.
     */
    fun clearAll() {
        val root = fileFromPaths(emptyArray())
        root.deleteRecursively()
    }

    /**
     * Формирует объект File на основе списка путей.
     * Создает необходимые директории, если они не существуют.
     *
     * Алгоритм:
     * 1. Начинает с базовой директории кэша.
     * 2. Добавляет начальные пути (startedPaths).
     * 3. Создает директории.
     * 4. Добавляет пути из аргумента paths.
     * 5. К последнему элементу добавляет расширение .json.
     *
     * @param paths Список путей
     * @return Объект файла [File], соответствующий указанному пути
     */
    private fun fileFromPaths(paths: Array<out String>): File {
        var file = cacheDir
        for (i in startedPaths.indices) {
            file = File(file, startedPaths[i])
        }
        file.mkdirs()

        for (i in paths.indices) {
            file = File(file, if (i == paths.size - 1) "${paths[i]}.json" else paths[i])
        }

        return file
    }

    companion object {
        private const val TAG = "CacheFolderLog"
    }
}