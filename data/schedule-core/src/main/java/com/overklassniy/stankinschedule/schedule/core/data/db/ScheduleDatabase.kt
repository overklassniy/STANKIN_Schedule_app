package com.overklassniy.stankinschedule.schedule.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * База данных Room для хранения расписаний и пар.
 *
 * Содержит таблицы:
 * - [ScheduleEntity] (расписания)
 * - [PairEntity] (пары)
 */
@Database(
    entities = [
        ScheduleEntity::class,
        PairEntity::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class ScheduleDatabase : RoomDatabase() {

    /**
     * Возвращает DAO для работы с таблицами расписания.
     *
     * @return Объект [ScheduleDao].
     */
    abstract fun schedule(): ScheduleDao

    companion object {
        /**
         * Singleton экземпляр базы данных.
         */
        @Volatile
        private var instance: ScheduleDatabase? = null

        /**
         * Получает или создает экземпляр базы данных.
         * Использует паттерн Singleton с двойной проверкой блокировки.
         *
         * @param context Контекст приложения.
         * @return Экземпляр [ScheduleDatabase].
         */
        fun database(context: Context): ScheduleDatabase {
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
                    ScheduleDatabase::class.java,
                    "schedule_database"
                ).fallbackToDestructiveMigration(true)

                val database = databaseBuilder.build()
                instance = database
                return database
            }
        }
    }
}