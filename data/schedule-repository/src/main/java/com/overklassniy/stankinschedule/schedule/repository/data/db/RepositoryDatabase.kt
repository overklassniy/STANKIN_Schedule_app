package com.overklassniy.stankinschedule.schedule.repository.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * База данных Room для хранения репозиториев расписаний.
 * Примечание: для экспорта схемы необходимо указать room.schemaLocation в build.gradle.kts
 * или установить exportSchema = false.
 */
@Database(
    entities = [
        RepositoryEntity::class,
    ],
    version = 2,
    exportSchema = true
)

abstract class RepositoryDatabase : RoomDatabase() {

    /**
     * Возвращает DAO для работы с репозиториями.
     *
     * @return DAO интерфейс
     */
    abstract fun repository(): RepositoryDao

    companion object {
        @Volatile
        private var instance: RepositoryDatabase? = null

        /**
         * Возвращает singleton экземпляр базы данных.
         *
         * @param context Контекст приложения
         * @return Экземпляр базы данных
         */
        fun database(context: Context): RepositoryDatabase {
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
                    RepositoryDatabase::class.java,
                    "schedule_repository_database"
                ).fallbackToDestructiveMigration(true)

                val database = databaseBuilder.build()
                instance = database
                return database
            }
        }
    }
}