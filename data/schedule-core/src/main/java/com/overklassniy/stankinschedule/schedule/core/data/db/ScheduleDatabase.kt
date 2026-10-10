package com.overklassniy.stankinschedule.schedule.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
    version = 4,
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
         * Миграция с версии 1 на 2: добавление столбца link в таблицу пар.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE schedule_pair_entities ADD COLUMN link TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        /**
         * Миграция с версии 2 на 3: добавление столбцов departments и email в таблицу пар.
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE schedule_pair_entities ADD COLUMN departments TEXT NOT NULL DEFAULT '[]'"
                )
                db.execSQL(
                    "ALTER TABLE schedule_pair_entities ADD COLUMN email TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        /**
         * Миграция с версии 3 на 4: обновление времён звонков до актуального расписания.
         *
         * Заменяет старые значения времени начала и окончания пар в столбце time
         * (формат "H:mm-H:mm") на новые. Начала и окончания обновляются раздельно,
         * так как значение "15:50" является одновременно новым временем начала (из "16:00")
         * и старым временем окончания (→ "15:40").
         */
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Старые времена начала пар → новые (заменяются по префиксу "H:mm-")
                db.execSQL("UPDATE schedule_pair_entities SET time = REPLACE(time, '10:20-', '10:15-') WHERE time LIKE '10:20-%'")
                db.execSQL("UPDATE schedule_pair_entities SET time = REPLACE(time, '14:10-', '14:05-') WHERE time LIKE '14:10-%'")
                db.execSQL("UPDATE schedule_pair_entities SET time = REPLACE(time, '16:00-', '15:50-') WHERE time LIKE '16:00-%'")
                // Старые времена окончания пар → новые (заменяются по суффиксу "-H:mm")
                db.execSQL("UPDATE schedule_pair_entities SET time = REPLACE(time, '-10:10', '-10:05') WHERE time LIKE '%-10:10'")
                db.execSQL("UPDATE schedule_pair_entities SET time = REPLACE(time, '-12:00', '-11:50') WHERE time LIKE '%-12:00'")
                db.execSQL("UPDATE schedule_pair_entities SET time = REPLACE(time, '-14:00', '-13:55') WHERE time LIKE '%-14:00'")
                db.execSQL("UPDATE schedule_pair_entities SET time = REPLACE(time, '-15:50', '-15:40') WHERE time LIKE '%-15:50'")
                db.execSQL("UPDATE schedule_pair_entities SET time = REPLACE(time, '-17:40', '-17:25') WHERE time LIKE '%-17:40'")
            }
        }

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
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration(true)

                val database = databaseBuilder.build()
                instance = database
                return database
            }
        }
    }
}
