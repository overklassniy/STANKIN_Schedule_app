package com.overklassniy.stankinschedule.news.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * База данных Room для хранения новостей.
 *
 * Содержит сущности:
 * - [NewsEntity]: Таблица новостей.
 *
 * Версия базы данных: 3.
 * Экспорт схемы включен.
 */
@Database(
    entities = [
        NewsEntity::class,
    ],
    version = 3,
    exportSchema = true
)
abstract class NewsDatabase : RoomDatabase() {

    /**
     * Возвращает DAO для работы с новостями.
     *
     * @return Объект [NewsDao].
     */
    abstract fun news(): NewsDao

    companion object {
        @Volatile
        private var instance: NewsDatabase? = null

        /**
         * Возвращает экземпляр базы данных (Singleton).
         * Если экземпляр еще не создан, создает его.
         * Использует блокировку для обеспечения потокобезопасности при создании.
         *
         * Примечание: Включена деструктивная миграция `fallbackToDestructiveMigration`,
         * что означает потерю данных при изменении версии базы данных, если миграция не описана явно.
         *
         * @param context Контекст приложения.
         * @return Экземпляр [NewsDatabase].
         */
        fun database(context: Context): NewsDatabase {
            val currentInstance = instance
            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val currentInstance2 = instance
                if (currentInstance2 != null) {
                    return currentInstance2
                }

                val databaseBuilder = Room.databaseBuilder(
                    context,
                    NewsDatabase::class.java,
                    "news_database"
                ).fallbackToDestructiveMigration(true)

                val database = databaseBuilder.build()
                instance = database
                return database
            }
        }
    }
}